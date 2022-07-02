package com.blockchaindotcom.core.actions

import com.blockchaindotcom.core.domain.OrderBook
import com.blockchaindotcom.core.domain.OrderEntriesRepository
import com.blockchaindotcom.core.domain.SymbolsRepository

class GetOrderBooks(
    private val symbolsRepository: SymbolsRepository,
    private val orderEntriesRepository: OrderEntriesRepository
) {
    operator suspend fun invoke(): List<OrderBook> {
        val symbols = symbolsRepository.get()

        val orderEntries = symbols.associateWith { orderEntriesRepository.get(it) }

        return symbols.map { symbol ->
            val orderEntriesForSymbol = orderEntries[symbol]!!
            val quantity = orderEntriesForSymbol.sumOf { it.quantity }
            val priceAvg = orderEntriesForSymbol.sumOf { it.price } / quantity

            OrderBook(symbol, priceAvg, quantity)
        }
    }
}
