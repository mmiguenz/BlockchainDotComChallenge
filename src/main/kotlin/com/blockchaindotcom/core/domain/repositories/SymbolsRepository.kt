package com.blockchaindotcom.core.domain.repositories

interface SymbolsRepository {
    suspend fun get(): List<String>
}
