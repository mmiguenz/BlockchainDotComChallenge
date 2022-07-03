package com.blockchaindotcom.providers

import com.blockchaindotcom.core.infrastructure.repositories.BlockChainDotComSymbolsRepository
import com.blockchaindotcom.core.infrastructure.repositories.DummyOrderEntriesRepository

object RepositoriesProvider {
    val OrderEntriesRepository by lazy {
        DummyOrderEntriesRepository()
    }

    val SymbolsRepository by lazy {
        BlockChainDotComSymbolsRepository(HttpClientProvider.getClient("api.blockchain.com"))
    }
}