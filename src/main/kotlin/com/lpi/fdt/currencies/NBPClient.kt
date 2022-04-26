package com.lpi.fdt.currencies

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lpi.fdt.config.createObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate

/**
 * Client calling National Polish Bank public API to get average exchange rates
 */
class NBPClient {

    private val baseUrl = "http://api.nbp.pl/api/exchangerates/rates/a"

    fun getCurrencyRates(symbol: String, dateFrom: LocalDate, dateTo: LocalDate): NBPCurrencyRatesResponse {
        val url = "$baseUrl/$symbol/$dateFrom/$dateTo"
        val request = HttpRequest.newBuilder(URI.create(url)).header("accept", "application/json").build()
        val client = HttpClient.newBuilder().build()
        val json = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        return createObjectMapper().readValue(json, NBPCurrencyRatesResponse::class.java)
    }

}

data class NBPCurrencyRatesResponse(
    val table: String,
    val currency: String,
    val code: String,
    val rates: List<NBPCurrencyRate>
)

data class NBPCurrencyRate(
    val no: String,
    val effectiveDate: LocalDate,
    val mid: Double //TODO BigDecimal
)