package com.blockchaindotcom.core.domain.repositories

import com.blockchaindotcom.core.domain.model.OrderEntry

interface OrderEntriesRepository {
    suspend fun get(symbol: String): List<OrderEntry>
}
