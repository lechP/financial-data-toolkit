package com.lpi.fdt.stocks

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

// TODO update KTOR
// TODO client calling Stooq for investment fund pricing
// TODO reuse take common part of ktor

// https://stooq.pl/q/d/l/?s=1501.n&i=d // i = interval ; s = symbol

class StooqClient {

    private val baseUrl = "https://stooq.pl/q/d/l/"

    suspend fun getHistoricalValues(symbol: String): String {
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
        }.body()
    }

}

fun main() {

    runBlocking {
        println(StooqClient().getHistoricalValues("PKN"))
    }
}