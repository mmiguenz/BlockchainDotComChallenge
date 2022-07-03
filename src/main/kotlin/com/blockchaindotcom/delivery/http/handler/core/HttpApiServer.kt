package com.blockchaindotcom.delivery.http.handler.core

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

class HttpApiServer(
    private vararg val handlers: Handler
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun start() {
        logger.info("Starting BlockChain Challenge API in port 8080")

        val server = embeddedServer(Netty, port = 8080) {
            main()
        }

        server.start(wait = true)
    }

    private fun Application.main() {
        installFeatures()

        routing {
            if (logger.isTraceEnabled)
                trace { logger.trace(it.buildText()) }
        }

        handlers.forEach { it.routing(this) }
    }

    private fun Application.installFeatures() {
        install(DefaultHeaders)
        installContentNegotiation()
        install(XForwardedHeaderSupport)
        install(CallLogging) {
            level = Level.INFO
        }

    }

    private fun Application.installContentNegotiation() {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }
}