package com.lpi.fdt.quotations.currencies

import com.lpi.fdt.config.ktorClient
import com.lpi.fdt.serialization.BigDecimalSerializer
import com.lpi.fdt.serialization.LocalDateSerializer
import com.lpi.fdt.util.chunkedDateRange
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Client calling National Polish Bank public API to get average exchange rates
 */
class NBPClient {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val baseUrl = "http://api.nbp.pl/api/exchangerates/rates/a"

    // Limit to avoid BAD_REQUEST: Limit of 367 days has been exceeded
    private val dateRangeLimit = 360

    suspend fun getCurrencyExchangeRates(
        symbol: String,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): List<CurrencyRates> =
        chunkedDateRange(dateFrom, dateTo, dateRangeLimit).map { (from, to) ->
            currencyRatesRequest(symbol, from, to)
        }

    private suspend fun currencyRatesRequest(
        symbol: String,
        from: LocalDate,
        to: LocalDate
    ): CurrencyRates {
        logger.info("Calling NBP Client for exchange rates for $symbol in range [$from,$to].")
        val response = ktorClient.get("$baseUrl/$symbol/$from/$to") {
            accept(ContentType.Application.Json)
        }
        return if (response.status.value == HttpStatusCode.NotFound.value) {
            logger.warn("Client responded with NOT FOUND for given date range. Returning empty response.")
            CurrencyRates.empty(symbol)
        } else {
            response.body<NBPCurrencyRatesResponse>().toCurrencyRates()
        }
    }
}

@Serializable
data class NBPCurrencyRatesResponse(
    val table: String,
    val currency: String,
    val code: String,
    val rates: List<NBPCurrencyRate>
) {
    fun toCurrencyRates() = CurrencyRates(
        currencyCode = code,
        rates = rates.map { it.toCurrencyRateValue() })
}

@Serializable
data class NBPCurrencyRate(
    val no: String,
    @Serializable(with = LocalDateSerializer::class)
    val effectiveDate: LocalDate,
    @Serializable(with = BigDecimalSerializer::class)
    val mid: BigDecimal
) {
    fun toCurrencyRateValue() = CurrencyRateValue(
        date = effectiveDate,
        value = mid
    )
}

data class CurrencyRates(
    val currencyCode: String,
    val rates: List<CurrencyRateValue>
) {
    companion object {
        fun empty(currencyCode: String) = CurrencyRates(currencyCode, emptyList())
    }
}

data class CurrencyRateValue(
    val date: LocalDate,
    val value: BigDecimal,
)