package com.blockchaindotcom.providers

import com.blockchaindotcom.core.infrastructure.repositories.BlockChainDotComOrderEntriesRepository
import com.blockchaindotcom.core.infrastructure.repositories.BlockChainDotComSymbolsRepository

object RepositoriesProvider {
    val OrderEntriesRepository by lazy {
        BlockChainDotComOrderEntriesRepository(HttpClientProvider.getClient("api.blockchain.com"))
    }

    val SymbolsRepository by lazy {
        BlockChainDotComSymbolsRepository(HttpClientProvider.getClient("api.blockchain.com"))
    }
}