package com.mzr.sm.job

import com.mzr.sm.event.KafkaProducer
import com.mzr.sm.security.SecurityServiceImpl
import org.apache.commons.lang3.time.DateUtils
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PasswordExpirationDetectionJob(
    val securityService: SecurityServiceImpl,
    val kafkaProducer: KafkaProducer,
) {

    @Scheduled(fixedDelay = DateUtils.MILLIS_PER_DAY)
    fun process() {
        securityService.authenticateAsServiceAccount("Default Service Account")
        val expiredPasswordEmails = listOf("some@mail.com")//TODO: replace with real implementation
        expiredPasswordEmails.forEach {
            kafkaProducer.sendMessage("passwordOutdated", it)
        }
    }
}