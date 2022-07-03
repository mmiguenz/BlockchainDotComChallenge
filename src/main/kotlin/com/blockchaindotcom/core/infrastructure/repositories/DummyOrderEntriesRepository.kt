package com.blockchaindotcom.core.infrastructure.repositories

import com.blockchaindotcom.core.domain.model.OrderEntry
import com.blockchaindotcom.core.domain.model.OrderType
import com.blockchaindotcom.core.domain.repositories.OrderEntriesRepository

class DummyOrderEntriesRepository: OrderEntriesRepository {
    override suspend fun get(symbol: String): List<OrderEntry> {
        return listOf(OrderEntry(OrderType.ASK, 1.0, 1.0))
    }
}