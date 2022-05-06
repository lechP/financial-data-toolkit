package com.lpi.fdt.currencies

import com.lpi.fdt.serialization.BigDecimalSerializer
import com.lpi.fdt.serialization.LocalDateSerializer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Client calling National Polish Bank public API to get average exchange rates
 */
class NBPClient {

    private val baseUrl = "http://api.nbp.pl/api/exchangerates/rates/a"

    suspend fun getCurrencyExchangeRates(symbol: String, dateFrom: LocalDate, dateTo: LocalDate): NBPCurrencyRatesResponse {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
        return client.get("$baseUrl/$symbol/$dateFrom/$dateTo") {
            accept(ContentType.Application.Json)
        }.body()
    }

}

@Serializable
data class NBPCurrencyRatesResponse(
    val table: String,
    val currency: String,
    val code: String,
    val rates: List<NBPCurrencyRate>
)

@Serializable
data class NBPCurrencyRate(
    val no: String,
    @Serializable(with = LocalDateSerializer::class)
    val effectiveDate: LocalDate,
    @Serializable(with = BigDecimalSerializer::class)
    val mid: BigDecimal
)