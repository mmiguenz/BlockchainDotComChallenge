package com.blockchaindotcom.core.actions

import com.blockchaindotcom.core.domain.exceptions.InvalidSymbolException
import com.blockchaindotcom.core.domain.model.OrderBook
import com.blockchaindotcom.core.domain.model.OrderEntry
import com.blockchaindotcom.core.domain.model.OrderType
import com.blockchaindotcom.core.domain.repositories.OrderEntriesRepository
import com.blockchaindotcom.core.domain.repositories.SymbolsRepository
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

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

        checkValidSymbolToFilter(symbols)

        val orderEntries = getOrderEntries(symbols, orderTypeToFilter)

        val orderBooksResult = buildOrderBooks(symbols, orderEntries)

        return orderBySymbol?.let { orderBooksResult.sortedBy { it.symbol } } ?: orderBooksResult
    }

    private fun buildOrderBooks(
        symbols: List<String>,
        orderEntries: ConcurrentHashMap<String, List<OrderEntry>>
    ): List<OrderBook> {
        val orderBooksResult = symbols.map { symbol ->
            val orderEntriesForSymbol = orderEntries[symbol]!!
            val quantity = orderEntriesForSymbol.sumOf { it.quantity }

            val priceAvg = if (quantity > 0)
                orderEntriesForSymbol.sumOf { it.price * it.quantity } / quantity
            else .0

            OrderBook(symbol, priceAvg, quantity)
        }
        return orderBooksResult
    }

    private fun checkValidSymbolToFilter(symbols: List<String>) {
        if (symbols.isEmpty())
            throw InvalidSymbolException()
    }

    private suspend fun getOrderEntries(
        symbols: List<String>,
        orderTypeToFilter: OrderType?
    ): ConcurrentHashMap<String, List<OrderEntry>> {
        val orderEntries = ConcurrentHashMap<String, List<OrderEntry>>()

        val getOrderEntriesAsyncJobs = symbols.map { symbol ->
            GlobalScope.launch {
                orderEntries[symbol] = orderEntriesRepository.get(symbol).filterByOrderType(orderTypeToFilter)
            }
        }

        for (job in getOrderEntriesAsyncJobs) {
            job.join()
        }

        return orderEntries
    }

    private fun List<String>.filterBySymbol(
        symbolToFilter: String?
    ) = symbolToFilter?.let { it -> this.filter { symbol -> symbol == it } } ?: this

    private fun List<OrderEntry>.filterByOrderType(orderTypeToFilter: OrderType?) =
        orderTypeToFilter?.let { it -> this.filter { orderEntry -> orderEntry.orderType == it } }
            ?: this

}


