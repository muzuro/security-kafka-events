package com.mzr.sm.event

import com.mzr.sm.security.EventSecurityServiceImpl
import mu.KotlinLogging
import org.apache.kafka.streams.kstream.ValueTransformerWithKey
import org.apache.kafka.streams.processor.ProcessorContext

class ExtractHeaderTransformer(
    val eventSecurityService: EventSecurityServiceImpl,
) : ValueTransformerWithKey<String?, String, Event> {

    val logger = KotlinLogging.logger {  }

    var context: ProcessorContext? = null
    override fun init(context: ProcessorContext) {
        this.context = context
    }

    override fun transform(readOnlyKey: String?, value: String): Event {
        val headers = context!!.headers()
        val authHeader = headers.headers("Authorization").first()
        val authentication = eventSecurityService.authenticate(String(authHeader.value()))
        logger.debug { "key: $readOnlyKey, value: $value, Auth: ${String(authHeader.value())}" }
        val headerEndIndex = value.indexOf(':')
        val eventType = value.substring(0, headerEndIndex)
        val body = value.substring(headerEndIndex + 1)

        return Event(eventType, body, authentication)
    }

    override fun close() {}
}