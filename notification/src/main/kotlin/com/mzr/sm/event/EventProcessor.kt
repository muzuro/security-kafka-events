package com.mzr.sm.event

import com.mzr.sm.security.EventSecurityServiceImpl
import com.mzr.sm.serivce.NotificationService
import mu.KotlinLogging
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import java.util.HashMap
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.common.serialization.Serdes
import org.springframework.beans.factory.annotation.Autowired
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.kstream.ValueTransformerWithKey
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class EventProcessor(
    val eventSecurityService: EventSecurityServiceImpl,
    val notificationService: NotificationService,
) {

    private val logger = KotlinLogging.logger {  }

    @Autowired
    fun buildPipeline(streamsBuilder: StreamsBuilder) {
        val messageStream = streamsBuilder
            .stream("events", Consumed.with(STRING_SERDE, STRING_SERDE))
        messageStream.transformValues(ValueTransformerWithKeySupplier<String?, String, Event> {
            ExtractHeaderTransformer(eventSecurityService)
        }).peek { key, event ->
            try {
                processEvent(key, event)
            } catch (e: Exception) {
                logger.error (e) { "error processing ${event.type}, key: $key, payload: ${event.value}" }
            }
        }
    }

    private fun processEvent(key: String?, event: Event) {
        if (event.type == "emailUpdated") {
            notificationService.verifyEmail(event.value)
        } else if (event.type == "passwordOutdated") {
            notificationService.notifyPasswordOutdated(event.value)
        } else {
            logger.info { "Not supported event type ${event.type}" }
        }
    }

    companion object {
        private val STRING_SERDE = Serdes.String()
    }
}