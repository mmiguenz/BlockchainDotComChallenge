package com.blockchaindotcom.providers

import com.blockchaindotcom.core.actions.GetOrderBooks

object ActionsProvider {
    val getOrderBooksAction by lazy {
        GetOrderBooks(RepositoriesProvider.SymbolsRepository, RepositoriesProvider.OrderEntriesRepository)
    }
}