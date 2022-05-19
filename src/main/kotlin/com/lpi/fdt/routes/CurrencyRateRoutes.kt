package com.lpi.fdt.routes

import com.lpi.fdt.currencies.NBPClient
import com.lpi.fdt.export.CsvCurrencyWriter
import com.lpi.fdt.export.CsvExchangeRateRecord
import com.lpi.fdt.export.CsvExportInput
import com.lpi.fdt.serialization.BigDecimalSerializer
import com.lpi.fdt.serialization.LocalDateSerializer
import io.ktor.http.*
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.io.File
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

        // TODO should be the same GET and JSON/CSV should be decided based on accept header
        get("/csv") {
            val symbol = call.request.queryParameters["symbol"] ?: "USD"
            val rawDateFrom = call.request.queryParameters["dateFrom"]
            val dateFrom = LocalDate.parse(rawDateFrom)
            val rawDateTo = call.request.queryParameters["dateTo"]
            val dateTo = LocalDate.parse(rawDateTo)

            val currencyRates = NBPClient().getCurrencyExchangeRates(symbol, dateFrom, dateTo)

            CsvCurrencyWriter.writeToFile(
                CsvExportInput(
                    currencyCode = currencyRates.code,
                    exchangeRates = currencyRates.rates.map { CsvExchangeRateRecord(it.effectiveDate, it.mid) }
                )
            )

            val filename = "PLNto${symbol}.csv" // TODO name should be result of Csv..Writer
            val file = File(filename)
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, filename)
                    .toString()
            )
            call.respondFile(file)
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