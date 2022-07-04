package com.blockchaindotcom.core.domain.exceptions

class InvalidOrderTypeException(orderType: String?) : Throwable("$orderType is not a valid order type") {

}
