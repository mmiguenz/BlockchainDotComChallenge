package com.blockchaindotcom.providers

import com.blockchaindotcom.core.infrastructure.repositories.DummyOrderEntriesRepository
import com.blockchaindotcom.core.infrastructure.repositories.DummySymbolsRepository

object RepositoriesProvider {
    val OrderEntriesRepository by lazy {
        DummyOrderEntriesRepository()
    }

    val SymbolsRepository by lazy {
        DummySymbolsRepository()
    }
}