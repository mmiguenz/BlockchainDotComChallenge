package com.blockchaindotcom.delivery.http.handler

import io.ktor.application.*

interface Handler {
    fun routing(a: Application)
}