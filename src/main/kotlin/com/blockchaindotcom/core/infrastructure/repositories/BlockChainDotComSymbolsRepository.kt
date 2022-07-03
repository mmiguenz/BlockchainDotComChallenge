package com.blockchaindotcom.core.infrastructure.repositories

import com.blockchaindotcom.core.domain.repositories.SymbolsRepository
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonObject

class BlockChainDotComSymbolsRepository(private val client: HttpClient): SymbolsRepository {
    override suspend fun get(): List<String> {
       val result =  client.get<JsonObject>("/v3/exchange/symbols")

        return result.keys.toList()
    }
}
