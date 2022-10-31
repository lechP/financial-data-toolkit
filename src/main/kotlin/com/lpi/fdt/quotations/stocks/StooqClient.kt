package com.lpi.fdt.quotations.stocks

import io.ktor.client.*
import io.ktor.client.call.body
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

class StooqClient : StocksClient {

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

class DummyStooqClient : StocksClient {
    override suspend fun getValueHistory(symbol: String): String =
        "initial,row,to,be,ignored\r\n2022-08-01,10.00,11.00,9.95,10.20,4000\r\n2022-08-02,10.10,10.10,9.35,9.50,2000"

}