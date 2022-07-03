package com.blockchaindotcom.delivery.http.handler.core

import com.blockchaindotcom.core.actions.GetOrderBooks
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GetOrderBooksHandler(private val getOrderBooks: GetOrderBooks? = null) : Handler {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)
    override fun routing(a: Application) {
        a.routing {
            route("/test") {
                get {
                    withContext(Dispatchers.IO) {
                        getOrderBooksHandler()
                    }
                }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.getOrderBooksHandler() {
        call.respond("OK")
    }
}