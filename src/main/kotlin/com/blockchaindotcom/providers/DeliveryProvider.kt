package com.blockchaindotcom.providers

import com.blockchaindotcom.delivery.http.handler.core.GetOrderBooksHandler
import com.blockchaindotcom.delivery.http.HttpApiServer

object DeliveryProvider {
    val getOrderBooksHandler by lazy {
        GetOrderBooksHandler(ActionsProvider.getOrderBooksAction)
    }
    val apiServer by lazy {
        HttpApiServer(
            getOrderBooksHandler
        )
    }
}