package com.blockchaindotcom.core.infrastructure.repositories

import com.blockchaindotcom.core.domain.model.OrderEntry
import com.blockchaindotcom.core.domain.model.OrderType
import com.blockchaindotcom.core.domain.repositories.OrderEntriesRepository
import io.ktor.client.*
import io.ktor.client.request.*

class BlockChainDotComOrderEntriesRepository(private val client: HttpClient) : OrderEntriesRepository {
    override suspend fun get(symbol: String): List<OrderEntry> {
        val orderBooks = client.get<OrderBookData>("/v3/exchange/l3/$symbol")
        return orderBooks.toOrderEntries()
    }
}

@kotlinx.serialization.Serializable
data class OrderBookData(val symbol: String, val bids: List<OrderEntryData>, val asks: List<OrderEntryData>) {
    fun toOrderEntries(): List<OrderEntry> {
        return asks.map { OrderEntry(OrderType.ASK, it.px, it.qty) } + bids.map {
            OrderEntry(
                OrderType.BID,
                it.px,
                it.qty
            )
        }
    }
}

@kotlinx.serialization.Serializable
data class OrderEntryData(val px: Double, val qty: Double, val num: Long)