package com.lpi.fdt.routes

import com.lpi.fdt.currencies.NBPClient
import com.lpi.fdt.serialization.BigDecimalSerializer
import com.lpi.fdt.serialization.LocalDateSerializer
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate

fun Route.currencyRateRouting() {
    route("/currency-rates") {
        get {
            // TODO validate query parameters
            val symbol = call.request.queryParameters["symbol"] ?: "USD"
            val rawDateFrom = call.request.queryParameters["dateFrom"]
            val dateFrom = LocalDate.parse(rawDateFrom)
            val rawDateTo = call.request.queryParameters["dateTo"]
            val dateTo = LocalDate.parse(rawDateTo)

            val currencyRates = NBPClient().getCurrencyExchangeRates(symbol, dateFrom, dateTo)

            val response = CurrencyRatesResponse(
                currencyCode = currencyRates.code,
                exchangeRates = currencyRates.rates.map {
                    CurrencyRate(
                        effectiveDate = it.effectiveDate,
                        exchangeRate = it.mid
                    )
                })

            call.respond(response)
        }
    }
}

@Serializable
data class CurrencyRatesResponse(
    val currencyCode: String,
    val exchangeRates: List<CurrencyRate>
)

@Serializable
data class CurrencyRate(
    @Serializable(with = LocalDateSerializer::class)
    val effectiveDate: LocalDate,
    @Serializable(with = BigDecimalSerializer::class)
    val exchangeRate: BigDecimal
)