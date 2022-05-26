package com.mzr.sm.security

import com.mzr.sm.security.service.ServiceAuthenticationProvider
import com.mzr.sm.security.service.ServiceAuthenticationToken
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class EventSecurityServiceImpl(
    private val serviceAuthenticationManager: ServiceAuthenticationProvider,
    private val providerManager: AuthenticationManager,
) {

    fun authenticate(authHeader: String): Authentication {
        val authentication = if (StringUtils.startsWithIgnoreCase(authHeader, "bearer")) {
            val bearerMatcher = SecurityServiceImpl.BEARER_PATTERN.matcher(authHeader)
            if (!bearerMatcher.matches()) {
                throw BadCredentialsException("Bearer token is malformed")
            } else {
                val token = bearerMatcher.group("token")
                val authenticationRequest = BearerTokenAuthenticationToken(token)
                providerManager.authenticate(authenticationRequest)
            }
        } else if (StringUtils.startsWithIgnoreCase(authHeader, "service")) {
            val serviceMatcher = SecurityServiceImpl.SERVICE_PATTERN.matcher(authHeader)
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

}