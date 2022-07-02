package com.blockchaindotcom.core.actions

import com.blockchaindotcom.core.domain.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GetOrderBooksTest {
    private lateinit var getOrderBooks: GetOrderBooks
    private lateinit var existentSymbols: List<String>
    private lateinit var existentOrderEntries: Map<String, List<OrderEntry>>
    private lateinit var actualOrderBooks: List<OrderBook>
    private lateinit var symbolsRepository: SymbolsRepository
    private lateinit var ordersEntriesRepository: OrderEntriesRepository

    @BeforeEach
    fun setUp() {
        symbolsRepository = mockk()
        ordersEntriesRepository = mockk()
        getOrderBooks = GetOrderBooks(symbolsRepository, ordersEntriesRepository)
    }

    @Test
    fun `Provides the quantity and price average of the order book (asks and bids) for each symbol`() = runTest {
        givenExistentSymbols()
        givenExistentOrderEntriesForEachSymbol()

        whenGetOrderBooks()

        shouldRetrieveOrderBooksForEachSymbol()
    }

    private fun givenExistentOrderEntriesForEachSymbol() {
        val ask = OrderEntry(OrderType.ASK, 100.5, 1.2)
        val bid = OrderEntry(OrderType.BID, 145.5, 3.2)

        existentOrderEntries = existentSymbols.associate { it to listOf(ask, bid) }
        existentSymbols.forEach { symbol ->
            coEvery { ordersEntriesRepository.get(symbol) } returns existentOrderEntries[symbol]!!
        }
    }

    private fun givenExistentSymbols() {
        existentSymbols = listOf(BTC_USD_SYMBOL, CEUR_USDT_SYMBOL, PAX_USD_SYMBOL, STX_USD_SYMBOL)
        coEvery { symbolsRepository.get() } returns existentSymbols
    }

    private fun shouldRetrieveOrderBooksForEachSymbol() {

        val expectedOrderBooks = existentSymbols.map { symbol ->
            val entries = existentOrderEntries[symbol]!!
            val totalQuantity = entries.sumOf { it.quantity }
            val priceAvg =  entries.sumOf { it.price } / totalQuantity

            OrderBook(symbol, priceAvg, totalQuantity)
        }

        assertEquals(expectedOrderBooks.count(), existentSymbols.count())
        assertEquals(expectedOrderBooks, actualOrderBooks)
    }

    private suspend fun whenGetOrderBooks() {
        actualOrderBooks = getOrderBooks()
    }

    companion object {
        private val BTC_USD_SYMBOL = "BTC-USD"
        private val CEUR_USDT_SYMBOL = "CEUR_USDT"
        private val PAX_USD_SYMBOL = "PAX-USD"
        private val STX_USD_SYMBOL = "STX_USD"
    }
}