package com.blockchaindotcom.core.actions

import com.blockchaindotcom.core.domain.*

class GetOrderBooks(
    private val symbolsRepository: SymbolsRepository,
    private val orderEntriesRepository: OrderEntriesRepository
) {
    operator suspend fun invoke(symbolToFilter: String?, orderTypeToFilter: OrderType?): List<OrderBook> {
        val symbols = symbolsRepository.get().filterBySymbol(symbolToFilter)

        val orderEntries = symbols.associateWith { orderEntriesRepository.get(it).filterByOrderType(orderTypeToFilter) }

        return symbols.map { symbol ->
            val orderEntriesForSymbol = orderEntries[symbol]!!
            val quantity = orderEntriesForSymbol.sumOf { it.quantity }
            val priceAvg = orderEntriesForSymbol.sumOf { it.price } / quantity

            OrderBook(symbol, priceAvg, quantity)
        }
    }

    private fun List<String>.filterBySymbol(
        symbolToFilter: String?
    ) = symbolToFilter?.let { it -> this.filter { symbol -> symbol == it } } ?: this

    private fun List<OrderEntry>.filterByOrderType(orderTypeToFilter: OrderType?) =
        orderTypeToFilter?.let { it -> this.filter { orderEntry -> orderEntry.orderType == it } }
            ?: this
}


