package com.blockchaindotcom.delivery.http

import com.blockchaindotcom.core.domain.exceptions.InvalidExchangeException
import com.blockchaindotcom.core.domain.exceptions.InvalidSymbolException
import com.blockchaindotcom.core.domain.exceptions.InvalidOrderTypeException
import com.blockchaindotcom.delivery.http.handler.Handler
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

class HttpApiServer(
    private vararg val handlers: Handler
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun start() {
        val port= System.getenv("PORT")?.toInt() ?: 8080
        logger.info("Starting BlockChain Challenge API in port $port")

        val server = embeddedServer(Netty, port = port) {
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
        install(StatusPages) {
            exceptionHandlers(logger)
        }
        installCORS()

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
    private fun Application.installCORS() {
        install(CORS) {
            anyHost()
        }
    }
    private fun StatusPages.Configuration.exceptionHandlers(logger: Logger) {
        exception<InvalidOrderTypeException> { cause ->
            logger.warn("${call.request.path()}: ${cause.localizedMessage}", cause)
            call.respond(HttpStatusCode.BadRequest, cause.message!!)
        }

        exception<InvalidSymbolException> { cause ->
            logger.warn("${call.request.path()}: ${cause.localizedMessage}", cause)
            call.respond(HttpStatusCode.BadRequest, cause.message!!)
        }

        exception<InvalidExchangeException> { cause ->
            logger.warn("${call.request.path()}: ${cause.localizedMessage}", cause)
            call.respond(HttpStatusCode.BadRequest, cause.message!!)
        }

        exception<Throwable> { cause ->
            logger.error("${call.request.path()}: ${cause.localizedMessage}", cause)
            call.respond(HttpStatusCode.InternalServerError, "Internal Error")
        }
    }
}