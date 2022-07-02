package com.blockchaindotcom.core.domain

interface OrderEntriesRepository {
    suspend fun get(symbol: String): List<OrderEntry>
}
