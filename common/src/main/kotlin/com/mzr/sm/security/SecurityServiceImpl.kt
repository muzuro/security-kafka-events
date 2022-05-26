package com.mzr.sm.security

import com.mzr.sm.security.service.ServiceAuthenticationProvider
import com.mzr.sm.security.service.ServiceAuthenticationToken
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.regex.Pattern

@Component
class SecurityServiceImpl(
    @Value("\${app.security.service.token}")
    private val serviceToken: String,
    private val serviceAuthenticationManager: ServiceAuthenticationProvider,
) {

    fun authenticateAsServiceAccount() {
        val authenticationResult = serviceAuthenticationManager.authenticate(ServiceAuthenticationToken(serviceToken))
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authenticationResult
        SecurityContextHolder.setContext(context)
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