package com.lpi.fdt.quotations.stocks

import com.lpi.fdt.config.ktorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface StocksClient {
    suspend fun getValueHistory(symbol: String): String
}

class StooqClient : StocksClient {

    private val baseUrl = "https://stooq.pl/q/d/l/"

    override suspend fun getValueHistory(symbol: String): String =
        ktorClient.get(baseUrl) {
            accept(ContentType.Text.CSV)
            parameter("s", symbol)
            parameter("i", "d")
        }.body() // i = interval ; s = symbol

}

class DummyStooqClient : StocksClient {
    override suspend fun getValueHistory(symbol: String): String =
        "initial,row,to,be,ignored\r\n2022-08-01,10.00,11.00,9.95,10.20,4000\r\n2022-08-02,10.10,10.10,9.35,9.50,2000"

}