package com.blockchaindotcom.core.infrastructure.repositories

import com.blockchaindotcom.core.domain.repositories.SymbolsRepository

class DummySymbolsRepository: SymbolsRepository {
    override suspend fun get(): List<String> {
        return listOf("BTC-USD", "ETH-USD")
    }
}