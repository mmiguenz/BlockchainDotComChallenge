package com.blockchaindotcom.delivery.http.handler.core

import com.blockchaindotcom.core.actions.GetOrderBooks
import com.blockchaindotcom.core.domain.model.ExchangeType
import com.blockchaindotcom.core.domain.model.OrderBook
import com.blockchaindotcom.core.domain.model.OrderType
import com.blockchaindotcom.delivery.http.handler.Handler
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GetOrderBooksHandler(private val getOrderBooks: GetOrderBooks) : Handler {
    override fun routing(a: Application) {
        a.routing {
            route("/exchanges/{exchange-name}/order-books") {
                get {
                    withContext(Dispatchers.IO) {
                        getOrderBooksHandler()
                    }
                }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.getOrderBooksHandler() {
        val exchangeName = call.parameters["exchange-name"].let { ExchangeType[it] }!!
        val symbol = call.request.queryParameters["symbol"]
        val orderType = call.request.queryParameters["type"]?.let { OrderType[it] }
        val orderBySymbol = call.request.queryParameters["sorted"].toBoolean()

        val result = getOrderBooks(exchangeName, symbol, orderType, orderBySymbol)
        call.respond(result.toOrderBookResponse())
    }
}

private fun List<OrderBook>.toOrderBookResponse(): List<OrderBooksResponse> =
    this.map { OrderBooksResponse(it.symbol, it.priceAvg, it.totalQuantity) }


@Serializable
data class OrderBooksResponse(
    val symbol: String,
    @SerialName("price_avg")
    val price: Double,
    val quantity: Double
)
