package com.lpi.fdt.stocks

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// TODO update KTOR
// TODO reuse take common part of ktor

// TODO make the whole thing runnable from console without intelliJ

// TODO expose endpoint to gather this data as well as currencies?
// TODO introduce some dummy cache or maybe move it all to Budgy?

interface StocksClient {
    suspend fun getValueHistory(symbol: String): String
}
class StooqClient: StocksClient {

    private val baseUrl = "https://stooq.pl/q/d/l/"

    override suspend fun getValueHistory(symbol: String): String {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
        return client.get(baseUrl) {
            accept(ContentType.Text.CSV)
            parameter("s", symbol)
            parameter("i", "d")
        }.body() // i = interval ; s = symbol
    }

}