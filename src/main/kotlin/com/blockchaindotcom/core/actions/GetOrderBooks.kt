package com.blockchaindotcom.core.actions

import com.blockchaindotcom.core.domain.model.OrderBook
import com.blockchaindotcom.core.domain.model.OrderEntry
import com.blockchaindotcom.core.domain.model.OrderType
import com.blockchaindotcom.core.domain.repositories.OrderEntriesRepository
import com.blockchaindotcom.core.domain.repositories.SymbolsRepository

class GetOrderBooks(
    private val symbolsRepository: SymbolsRepository,
    private val orderEntriesRepository: OrderEntriesRepository
) {
    suspend operator fun invoke(
        symbolToFilter: String?,
        orderTypeToFilter: OrderType?,
        orderBySymbol: Boolean?
    ): List<OrderBook> {
        val symbols = symbolsRepository.get().filterBySymbol(symbolToFilter)

        val orderEntries = symbols.associateWith { orderEntriesRepository.get(it).filterByOrderType(orderTypeToFilter) }

        val orderBooksResult = symbols.map { symbol ->
            val orderEntriesForSymbol = orderEntries[symbol]!!
            val quantity = orderEntriesForSymbol.sumOf { it.quantity }
            val priceAvg = orderEntriesForSymbol.sumOf { it.price } / quantity

            OrderBook(symbol, priceAvg, quantity)
        }

        return orderBySymbol?.let { orderBooksResult.sortedBy { it.symbol } } ?: orderBooksResult
    }

    private fun List<String>.filterBySymbol(
        symbolToFilter: String?
    ) = symbolToFilter?.let { it -> this.filter { symbol -> symbol == it } } ?: this

    private fun List<OrderEntry>.filterByOrderType(orderTypeToFilter: OrderType?) =
        orderTypeToFilter?.let { it -> this.filter { orderEntry -> orderEntry.orderType == it } }
            ?: this
}


