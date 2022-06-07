package com.mzr.sm.security

import com.mzr.sm.security.service.ServiceAccountRepository
import com.mzr.sm.security.service.ServiceAuthenticationProvider
import com.mzr.sm.security.service.ServiceAuthenticationToken
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.regex.Pattern

@Component
class SecurityServiceImpl(
    private val serviceAccountRepository: ServiceAccountRepository,
    private val serviceAuthenticationManager: ServiceAuthenticationProvider,
    private val providerManager: AuthenticationManager,
) {
    fun authenticateAsServiceAccount(serviceAccountName: String) {
        val serviceAccount = serviceAccountRepository.findServiceAccountByName(serviceAccountName)
        val authenticationResult = serviceAuthenticationManager.authenticate(ServiceAuthenticationToken(serviceAccount.token))
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authenticationResult
        SecurityContextHolder.setContext(context)
    }
    fun authenticate(authHeader: String): Authentication {
        val authentication = if (StringUtils.startsWithIgnoreCase(authHeader, "bearer")) {
            val bearerMatcher = BEARER_PATTERN.matcher(authHeader)
            if (!bearerMatcher.matches()) {
                throw BadCredentialsException("Bearer token is malformed")
            } else {
                val token = bearerMatcher.group("token")
                val authenticationRequest = BearerTokenAuthenticationToken(token)
                providerManager.authenticate(authenticationRequest)
            }
        } else if (StringUtils.startsWithIgnoreCase(authHeader, "service")) {
            val serviceMatcher = SERVICE_PATTERN.matcher(authHeader)
            if (!serviceMatcher.matches()) {
                throw BadCredentialsException("Bearer token is malformed")
            } else {
                val token = serviceMatcher.group("token")
                val authenticationRequest = ServiceAuthenticationToken(token)
                serviceAuthenticationManager.authenticate(authenticationRequest)
            }
        } else {
            throw AuthenticationCredentialsNotFoundException("Not found authentication header")
        }
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        SecurityContextHolder.setContext(context)
        return authentication
    }
    fun createAuthHeaderString(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication is ServiceAuthenticationToken) {
            "Service ${authentication.token}"
        } else if (authentication is JwtAuthenticationToken) {
            "Bearer ${authentication.token.tokenValue}"
        } else {
            throw IllegalStateException("Unknown security context")
        }
    }
    companion object {
        val BEARER_PATTERN = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE)
        val SERVICE_PATTERN = Pattern.compile("^Service (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE)
    }
}