package com.blockchaindotcom.core.domain.exceptions

class InvalidExchangeException(exchangeType: String?) : Throwable("$exchangeType is not a valid exchange") {

}
