package com.blockchaindotcom.core.domain.model

import com.blockchaindotcom.core.domain.exceptions.UnknownOrderType

enum class OrderType {
    ASK,
    BID;

    companion object {
        operator fun get(orderType: String?): OrderType? {
            try {
                return when (orderType.orEmpty().trim()) {
                    "" -> null
                    else -> valueOf(orderType!!)
                }

            } catch (ex: Throwable) {
                throw UnknownOrderType()
            }
        }
    }
}
