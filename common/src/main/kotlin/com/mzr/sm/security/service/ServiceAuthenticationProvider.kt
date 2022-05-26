package com.mzr.sm.security.service

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class ServiceAuthenticationProvider(
    private val serviceAccountRepository: ServiceAccountRepository
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val token = (authentication as ServiceAuthenticationToken).token
        val serviceAccount = serviceAccountRepository.findServiceAccount(token)
        return if (serviceAccount != null) {
            val authorities = serviceAccount.permissions.map { SimpleGrantedAuthority(it) }
            ServiceAuthenticationToken(token, authorities)
        } else {
            throw AuthenticationServiceException("Unknown service ${authentication.name}")
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == ServiceAuthenticationToken::class.java
    }
}