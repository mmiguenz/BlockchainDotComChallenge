package com.blockchaindotcom.delivery.http.handler.core

import io.ktor.application.*

interface Handler {
    fun routing(a: Application)
}