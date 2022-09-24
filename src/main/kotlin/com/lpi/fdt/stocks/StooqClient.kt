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
import java.math.BigDecimal
import java.time.LocalDate

// TODO update KTOR
// TODO client calling Stooq for investment fund pricing
// TODO reuse take common part of ktor

class StooqClient {

    private val baseUrl = "https://stooq.pl/q/d/l/"

    // TODO split into pure client and sort of a facade to properly test it

    suspend fun getHistoricalValues(symbol: String): List<StooqRecord> =
        getHistoricalValuesRaw(symbol).parseStooqResponse()

    private fun String.parseStooqResponse() =
        split("\r\n").drop(1).filter { it.isNotBlank() }.map { it.toStooqRecord() }

    private fun String.toStooqRecord(): StooqRecord {
        println(this)
        return split(",").let {
            StooqRecord(
                date = LocalDate.parse(it[0]),
                open = BigDecimal(it[1]),
                max = BigDecimal(it[2]),
                min = BigDecimal(it[3]),
                close = BigDecimal(it[4]),
                volume = if (it.size > 5) BigDecimal(it[5]) else null
            )
        }
    }

    private suspend fun getHistoricalValuesRaw(symbol: String): String {
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

fun main() {

    runBlocking {
        println(StooqClient().getHistoricalValues("PKN"))
    }
}

data class StooqRecord(
    val date: LocalDate,
    val open: BigDecimal,
    val max: BigDecimal,
    val min: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal?
)