package com.mzr.sm.event

import com.mzr.sm.security.SecurityServiceImpl
import com.mzr.sm.serivce.NotificationService
import mu.KotlinLogging
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EventProcessor(
    val securityService: SecurityServiceImpl,
    val notificationService: NotificationService,
) {

    private val logger = KotlinLogging.logger {  }

    @Autowired
    fun buildPipeline(streamsBuilder: StreamsBuilder) {
        val messageStream = streamsBuilder.stream("events", Consumed.with(STRING_SERDE, STRING_SERDE))
        messageStream.transformValues(ValueTransformerWithKeySupplier<String?, String, Event> {
            EventTransformer()
        }).peek { key, event ->
            try {
                securityService.authenticate(event.authHeader)
                processEvent(key, event)
            } catch (e: Exception) {
                logger.error (e) { "error processing ${event.type}, key: $key, payload: ${event.value}" }
            }
        }
    }

    private fun processEvent(key: String?, event: Event) {
        if (event.type == "emailUpdated") {
            notificationService.sendVerificationEmail(event.value)
        } else
        if (event.type == "passwordOutdated") {
            notificationService.notifyPasswordOutdated(event.value)
        } else {
            logger.info { "Not supported event type ${event.type}" }
        }
    }

    companion object {
        private val STRING_SERDE = Serdes.String()
    }
}