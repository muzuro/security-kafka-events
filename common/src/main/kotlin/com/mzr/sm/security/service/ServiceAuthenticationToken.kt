package com.mzr.sm.security.service

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class ServiceAuthenticationToken(
    val token: String,
    authorities: List<GrantedAuthority> = emptyList()
): AbstractAuthenticationToken(authorities) {
    override fun getCredentials(): Any {
        return token
    }
    override fun getPrincipal(): Any {
        return token
    }
}