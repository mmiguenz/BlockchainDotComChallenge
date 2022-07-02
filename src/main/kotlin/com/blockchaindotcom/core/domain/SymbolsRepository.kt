package com.blockchaindotcom.core.domain

interface SymbolsRepository {
    suspend fun get(): List<String>
}
