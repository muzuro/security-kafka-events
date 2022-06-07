package com.mzr.sm.event

import mu.KotlinLogging
import org.apache.kafka.streams.kstream.ValueTransformerWithKey
import org.apache.kafka.streams.processor.ProcessorContext

class EventTransformer : ValueTransformerWithKey<String?, String, Event> {

    val logger = KotlinLogging.logger {  }

    var context: ProcessorContext? = null
    override fun init(context: ProcessorContext) {
        this.context = context
    }

    override fun transform(readOnlyKey: String?, value: String): Event {
        val headers = context!!.headers()
        val authHeader = String(headers.headers("Authorization").first().value())
        logger.debug { "key: $readOnlyKey, value: $value, Auth: $authHeader" }
        val headerEndIndex = value.indexOf(':')
        val eventType = value.substring(0, headerEndIndex)
        val body = value.substring(headerEndIndex + 1)

        return Event(eventType, body, authHeader)
    }

    override fun close() {}
}