@file:OptIn(ExperimentalCoroutinesApi::class)

package com.blockchaindotcom.core.actions

import com.blockchaindotcom.core.domain.exceptions.InvalidSymbolException
import com.blockchaindotcom.core.domain.model.ExchangeType
import com.blockchaindotcom.core.domain.model.OrderBook
import com.blockchaindotcom.core.domain.model.OrderEntry
import com.blockchaindotcom.core.domain.model.OrderType
import com.blockchaindotcom.core.domain.repositories.OrderEntriesRepository
import com.blockchaindotcom.core.domain.repositories.SymbolsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class GetOrderBooksTest {
    private lateinit var exchangeType: ExchangeType
    private var orderBySymbol: Boolean? = null
    private var symbolToFilter: String? = null
    private var orderTypeToFilter: OrderType? = null
    private lateinit var getOrderBooks: GetOrderBooks
    private lateinit var existentSymbols: List<String>
    private lateinit var existentOrderEntries: Map<String, List<OrderEntry>>
    private lateinit var actualOrderBooks: List<OrderBook>
    private lateinit var symbolsRepository: SymbolsRepository
    private lateinit var ordersEntriesRepository: OrderEntriesRepository
    private lateinit var symbolsRepositories: Map<ExchangeType, SymbolsRepository>
    private lateinit var ordersEntriesRepositories: Map<ExchangeType, OrderEntriesRepository>

    @BeforeEach
    fun setUp() {
        symbolsRepository = mockk()
        ordersEntriesRepository = mockk()
        symbolsRepositories = mapOf(ExchangeType.BLOCKCHAIN_DOT_COM to symbolsRepository)
        ordersEntriesRepositories = mapOf(ExchangeType.BLOCKCHAIN_DOT_COM to ordersEntriesRepository)
        getOrderBooks = GetOrderBooks(symbolsRepositories, ordersEntriesRepositories)
    }

    @Test
    fun `given a valid exchange should provide the quantity and price average of the order book (asks and bids) for each symbol`() = runTest {
        givenAValidExchange()
        givenExistentSymbols()
        givenExistentOrderEntriesForEachSymbol()

        whenGetOrderBooks()

        shouldRetrieveOrderBooksForEachSymbol()
    }

    private fun givenAValidExchange() {
        exchangeType = ExchangeType.BLOCKCHAIN_DOT_COM
    }

    @Test
    fun `given a symbol and an order type results should be filter out`() = runTest {
        givenAValidExchange()
        givenExistentSymbols()
        givenExistentOrderEntriesForEachSymbol()
        givenASymbolToFilter()
        givenAnOrderTypeToFilter()

        whenGetOrderBooks()

        shouldRetrieveOrderBooksFilteredBySymbolAndType()
    }

    @Test
    fun `given a symbol results should be filter out`() = runTest {
        givenAValidExchange()
        givenExistentSymbols()
        givenExistentOrderEntriesForEachSymbol()
        givenASymbolToFilter()
        whenGetOrderBooks()

        shouldRetrieveOrderBooksFilteredBySymbol()
    }

    @Test
    fun `given a OrderBySymbol parameter results should be ordered by symbol alphabetically`() = runTest {
        givenAValidExchange()
        givenExistentSymbols()
        givenExistentOrderEntriesForEachSymbol()
        givenAOrderBySymbolParam()
        whenGetOrderBooks()

        shouldRetrieveOrderBooksOrderedAlphabetically()
    }

    @Test
    fun `given a an empty list of orderEntries price avg should be 0`() = runTest {
        givenAValidExchange()
        givenExistentSymbols()
        givenNonExistentOrderEntriesForEachSymbol()

        whenGetOrderBooks()

        shouldRetrieveOrderBooksWithPriceAvgDefaultedToZero()
    }

    @Test
    fun `given a an invalid symbol should fail`() = runTest {
        givenAValidExchange()
        givenExistentSymbols()
        givenASymbolToFilter("Invalid-Symbol")

        assertFailsWith<InvalidSymbolException> { whenGetOrderBooks() }
    }

    private fun givenAOrderBySymbolParam() {
        orderBySymbol = true
    }

    private fun givenAnOrderTypeToFilter() {
        orderTypeToFilter = OrderType.ASK
    }

    private fun givenASymbolToFilter(symbol: String = BTC_USD_SYMBOL) {
        symbolToFilter = symbol
    }

    private fun givenExistentOrderEntriesForEachSymbol() {
        val ask = OrderEntry(OrderType.ASK, ASK_PRICE, ASK_QUANTITY)
        val bid = OrderEntry(OrderType.BID, BID_PRICE, BID_QUANTITY)

        existentOrderEntries = existentSymbols.associate { it to listOf(ask, bid) }
        existentSymbols.forEach { symbol ->
            coEvery { ordersEntriesRepository.get(symbol) } returns existentOrderEntries[symbol]!!
        }
    }

    private fun givenNonExistentOrderEntriesForEachSymbol() {
        coEvery { ordersEntriesRepository.get(any()) } returns listOf()
    }

    private fun givenExistentSymbols() {
        existentSymbols = listOf(PAX_USD_SYMBOL, BTC_USD_SYMBOL, STX_USD_SYMBOL, CEUR_USDT_SYMBOL)
        coEvery { symbolsRepository.get() } returns existentSymbols
    }

    private suspend fun whenGetOrderBooks() {
        actualOrderBooks = getOrderBooks(exchangeType, symbolToFilter, orderTypeToFilter, orderBySymbol)
    }

    private fun shouldRetrieveOrderBooksForEachSymbol() {
        assertEquals(orderBooks.count(), existentSymbols.count())
        assertEquals(orderBooks.toSet(), actualOrderBooks.toSet())

    }

    private fun shouldRetrieveOrderBooksFilteredBySymbolAndType() {
        val orderBooksFiltered = listOf(OrderBook_BTC_USD_Only_ASK)
        assertEquals(orderBooksFiltered, actualOrderBooks)
    }

    private fun shouldRetrieveOrderBooksFilteredBySymbol() {
        val orderBooksFiltered = listOf(OrderBook_BTC_USD)
        assertEquals(orderBooksFiltered, actualOrderBooks)
    }

    private fun shouldRetrieveOrderBooksOrderedAlphabetically() {
        assertEquals(orderBooks.sortedBy { it.symbol }, actualOrderBooks)
    }

    private fun shouldRetrieveOrderBooksWithPriceAvgDefaultedToZero() {
        val orderBooksDefaulted = existentSymbols.map { OrderBook(it, 0.0, 0.0) }
        assertEquals(orderBooksDefaulted.toSet(), actualOrderBooks.toSet())
    }

    companion object {
        private const val ASK_PRICE = 100.0
        private const val BID_PRICE = 200.0
        private const val ASK_QUANTITY = 2.0
        private const val BID_QUANTITY = 3.0
        private const val ORDER_TOTAL_QUANTITY = ASK_QUANTITY + BID_QUANTITY
        private const val ORDER_PRICE_AVG = (ASK_PRICE * ASK_QUANTITY + BID_PRICE * BID_QUANTITY) / ORDER_TOTAL_QUANTITY
        private const val ORDER_PRICE_AVG_ONLY_ASK = (ASK_PRICE * ASK_QUANTITY) / (ASK_QUANTITY)
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