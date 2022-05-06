package com.lpi.fdt

import com.lpi.fdt.currencies.NBPClient
import com.lpi.fdt.export.CsvExchangeRateRecord
import com.lpi.fdt.export.CsvCurrencyWriter
import com.lpi.fdt.export.CsvExportInput
import kotlinx.coroutines.runBlocking
import java.time.LocalDate


fun main() {

    val startDate = LocalDate.of(2022, 4, 1)
    val endDate = LocalDate.of(2022, 5, 4)

    runBlocking {
        writeCurrencyRates("EUR", startDate, endDate)
        writeCurrencyRates("USD", startDate, endDate)
    }

}

/** Get currency rates against PLN from Polish National Bank (via its API) and export results to CSV file */
suspend fun writeCurrencyRates(symbol: String, startDate: LocalDate, endDate: LocalDate) {
    val currencyRates = NBPClient().getCurrencyExchangeRates(symbol, startDate, endDate)

    CsvCurrencyWriter.writeToFile(
        CsvExportInput(
            currencyCode = currencyRates.code,
            exchangeRates = currencyRates.rates.map { CsvExchangeRateRecord(it.effectiveDate, it.mid) }
        )
    )
}