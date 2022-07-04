package com.blockchaindotcom.providers

import com.blockchaindotcom.core.domain.model.ExchangeType
import com.blockchaindotcom.core.infrastructure.repositories.BlockChainDotComOrderEntriesRepository
import com.blockchaindotcom.core.infrastructure.repositories.BlockChainDotComSymbolsRepository

object RepositoriesProvider {
    private val blockChainOrderEntriesRepository by lazy {
        BlockChainDotComOrderEntriesRepository(HttpClientProvider.getClient("api.blockchain.com"))
    }

    private val blockChainSymbolsRepository by lazy {
        BlockChainDotComSymbolsRepository(HttpClientProvider.getClient("api.blockchain.com"))
    }

    val orderEntriesRepositories by lazy {
        mapOf(ExchangeType.BLOCKCHAIN_DOT_COM to blockChainOrderEntriesRepository)
    }

    val symbolsRepositories by lazy {
        mapOf(ExchangeType.BLOCKCHAIN_DOT_COM to blockChainSymbolsRepository)
    }
}