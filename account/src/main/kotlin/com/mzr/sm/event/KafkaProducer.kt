package com.mzr.sm.event

import com.mzr.sm.security.SecurityServiceImpl
import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
    val securityService: SecurityServiceImpl,
    val kafkaTemplate: KafkaTemplate<String, String>
) {

    private val logger = KotlinLogging.logger {  }

    fun sendMessage(eventType: String, message: String) {
        val payload = "$eventType:$message"
        val record = ProducerRecord<String, String>(TOPIC, payload)
        record.headers().add("Authorization", securityService.createAuthHeaderString().toByteArray())
        kafkaTemplate.send(record).addCallback(
            { logger.info { "Payload sent to topic: $payload" } },
            { ex -> logger.error(ex) { "Failed to send message" } }
        )
    }

    companion object {
        const val TOPIC = "events"
    }

}