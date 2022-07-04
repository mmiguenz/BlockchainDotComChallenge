package com.blockchaindotcom.core.domain.model

import com.blockchaindotcom.core.domain.exceptions.InvalidExchangeException

enum class ExchangeType(private val exchangeName: String) {
    BLOCKCHAIN_DOT_COM("blockchaindotcom");

    companion object {
        operator fun get(exchangeType: String?): ExchangeType? {
            try {
                return values().find { it.exchangeName == exchangeType }!!
            } catch (ex: Throwable) {
                throw InvalidExchangeException(exchangeType)
            }
        }
    }
}
