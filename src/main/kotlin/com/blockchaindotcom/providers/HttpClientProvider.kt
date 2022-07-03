package com.blockchaindotcom.providers

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*

class HttpClientProvider {

    companion object {
        private const val CONNECT_TIMEOUT = 200
        private const val REQUEST_TIMEOUT = 5000

        fun getClient(
            baseUrl: String
        ): HttpClient {

            return HttpClient(engine) {
                install(HttpTimeout) {
                    connectTimeoutMillis = CONNECT_TIMEOUT.toLong()
                    requestTimeoutMillis = REQUEST_TIMEOUT.toLong()
                }

                install(JsonFeature) {
                    serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                        this.ignoreUnknownKeys = true
                    })
                }

                defaultRequest {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = baseUrl
                    }
                }
            }
        }

        private val engine by lazy {
            Apache.create {
                connectTimeout = CONNECT_TIMEOUT
                connectionRequestTimeout = REQUEST_TIMEOUT
            }
        }
    }
}