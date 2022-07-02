package com.blockchaindotcom.core.actions

import com.blockchaindotcom.core.domain.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GetOrderBooksTest {
    private var symbolToFilter: String? = null
    private var orderTypeToFilter: OrderType? = null
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
    fun `should provide the quantity and price average of the order book (asks and bids) for each symbol`() = runTest {
        givenExistentSymbols()
        givenExistentOrderEntriesForEachSymbol()

        whenGetOrderBooks()

        shouldRetrieveOrderBooksForEachSymbol()
    }

    @Test
    fun `given a symbol and an order type results should be filter out`() = runTest {
        givenExistentSymbols()
        givenExistentOrderEntriesForEachSymbol()
        givenASymbolToFilter()
        givenAnOrderTypeToFilter()

        whenGetOrderBooks()

        shouldRetrieveOrderBooksFilteredBySymbolAndType()
    }

    private fun givenAnOrderTypeToFilter() {
        orderTypeToFilter = OrderType.ASK
    }

    private fun givenASymbolToFilter() {
        symbolToFilter = BTC_USD_SYMBOL
    }

    private fun givenExistentOrderEntriesForEachSymbol() {
        val ask = OrderEntry(OrderType.ASK, ASK_PRICE, ASK_QUANTITY)
        val bid = OrderEntry(OrderType.BID, BID_PRICE, BID_QUANTITY)

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
        assertEquals(orderBooks.count(), existentSymbols.count())
        assertEquals(orderBooks, actualOrderBooks)

    }

    private fun shouldRetrieveOrderBooksFilteredBySymbolAndType() {
        val orderBooksFiltered =  listOf(OrderBook_BTC_USD_Only_ASK)
        assertEquals(orderBooksFiltered, actualOrderBooks)
    }

    private suspend fun whenGetOrderBooks() {
        actualOrderBooks = getOrderBooks(symbolToFilter, orderTypeToFilter)
    }

    companion object {
        private const val ASK_PRICE = 100.0
        private const val BID_PRICE = 200.0
        private const val ASK_QUANTITY = 2.0
        private const val BID_QUANTITY = 3.0
        private const val ORDER_PRICE_AVG = 60.0
        private const val ORDER_PRICE_AVG_ONLY_ASK = 50.0
        private const val ORDER_TOTAL_QUANTITY = 5.0
        private const val BTC_USD_SYMBOL = "BTC-USD"
        private const val CEUR_USDT_SYMBOL = "CEUR_USDT"
        private const val PAX_USD_SYMBOL = "PAX-USD"
        private const val STX_USD_SYMBOL = "STX_USD"

        private val OrderBook_BTC_USD_Only_ASK = OrderBook(
            BTC_USD_SYMBOL,
            ORDER_PRICE_AVG_ONLY_ASK,
            ASK_QUANTITY
        )

        private val OrderBook_BTC_USD = OrderBook(
            BTC_USD_SYMBOL,
            ORDER_PRICE_AVG,
            ORDER_TOTAL_QUANTITY
        )
        private val OrderBook_CEUR_USDT = OrderBook(
            CEUR_USDT_SYMBOL,
            ORDER_PRICE_AVG,
            ORDER_TOTAL_QUANTITY
        )
        private val OrderBook_PAX_USD = OrderBook(
            PAX_USD_SYMBOL,
            ORDER_PRICE_AVG,
            ORDER_TOTAL_QUANTITY
        )
        private val OrderBook_STX_USD = OrderBook(
            STX_USD_SYMBOL,
            ORDER_PRICE_AVG,
            ORDER_TOTAL_QUANTITY
        )

        private val orderBooks = listOf(OrderBook_BTC_USD, OrderBook_CEUR_USDT, OrderBook_PAX_USD, OrderBook_STX_USD)

    }
}