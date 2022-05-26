package com.mzr.sm.service

import com.mzr.sm.event.KafkaProducer
import mu.KotlinLogging
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(
    private val kafkaProducer: KafkaProducer
) {

    private val logger = KotlinLogging.logger {  }

    @PreAuthorize("hasAuthority('email.update')")
    fun updateEmail(newEmail: String) {
        logger.info { "TODO: update email in user storage" }
        kafkaProducer.sendMessage("emailUpdated", newEmail)
    }

}