package com.mzr.sm.security.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ServiceAccountRepository(
    @Value("\${app.security.service.token}")
    private val serviceToken: String,
) {

    val storage = mutableListOf(ServiceAccount("Default Service Account", serviceToken, listOf("password.outdated")))

    fun findServiceAccountByName(name: String) = storage.first { it.name == name }

    fun findServiceAccountByToken(token: String) = storage.firstOrNull { it.token == token }

    fun removeServiceAccount(token: String) = storage.removeIf { it.token == token }

}