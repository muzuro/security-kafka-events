package com.mzr.sm.job

import com.mzr.sm.event.KafkaProducer
import com.mzr.sm.security.SecurityServiceImpl
import mu.KotlinLogging
import org.apache.commons.lang3.time.DateUtils
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DailyNotificationJob(
    val securityService: SecurityServiceImpl,
    val kafkaProducer: KafkaProducer,
) {

    private val logger = KotlinLogging.logger {  }

    @Scheduled(fixedDelay = DateUtils.MILLIS_PER_DAY)
    fun process() {
        securityService.authenticateAsServiceAccount()
        val outdatedPasswordEmails = listOf("some@mail.com")
        outdatedPasswordEmails.forEach {
            kafkaProducer.sendMessage("passwordOutdated", it)
        }
    }
}