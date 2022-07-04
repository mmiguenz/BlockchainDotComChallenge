package com.blockchaindotcom.core.domain.exceptions

class InvalidSymbolException(symbol: String?) : Throwable("$symbol is not a valid symbol") {

}
