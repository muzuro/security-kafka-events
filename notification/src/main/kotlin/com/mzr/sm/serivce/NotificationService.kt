package com.mzr.sm.serivce

import mu.KotlinLogging
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class NotificationService {

    val logger = KotlinLogging.logger {  }

    @PreAuthorize("hasAuthority('email.update')")
    fun verifyEmail(email: String) {
        logger.info { "TODO: send verify email to $email" }
    }

    @PreAuthorize("hasAuthority('password.outdated')")
    fun notifyPasswordOutdated(email: String) {
        logger.info { "TODO: send password outdated email to $email" }
    }

}