package com.mzr.sm

fun main(args: Array<String>) {
    val value = "emailUpdated:some@mail.com"
    val headerEndIndex = value.indexOf(':')
    val eventType = value.substring(0, headerEndIndex)
    val body = value.substring(headerEndIndex + 1)
    println("type: $eventType, body: $body")
}