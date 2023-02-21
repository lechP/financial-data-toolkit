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
    ): List<NBPCurrencyRatesResponse> =
        chunkedDateRange(dateFrom, dateTo, dateRangeLimit).map { (from, to) ->
            currencyRatesRequest(symbol, from, to)
        }

    private suspend fun currencyRatesRequest(
        symbol: String,
        from: LocalDate,
        to: LocalDate
    ): NBPCurrencyRatesResponse {
        logger.info("Calling NBP Client for exchange rates for $symbol in range [$from,$to].")
        return ktorClient.get("$baseUrl/$symbol/$from/$to") {
            accept(ContentType.Application.Json)
        }.body()
    }

    // TODO handle 404 when asking for /today/today when data is not yet published

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