package com.blockchaindotcom.providers

import com.blockchaindotcom.delivery.http.handler.core.GetOrderBooksHandler
import com.blockchaindotcom.delivery.http.handler.core.HttpApiServer

object DeliveyProvider {
    val getOrderBooksHandler by lazy {
        GetOrderBooksHandler()
    }
    val apiServer by lazy {
        HttpApiServer(
            getOrderBooksHandler
        )
    }
}