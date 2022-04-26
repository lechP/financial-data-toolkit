package com.lpi.fdt

import com.lpi.fdt.currencies.NBPClient
import com.lpi.fdt.export.CsvExchangeRateRecord
import com.lpi.fdt.export.CsvCurrencyWriter
import com.lpi.fdt.export.CsvExportInput
import java.time.LocalDate


fun main() {

    /** Get currency rates against PLN from Polish National Bank (via its API) and export results to CSV file */

    val currencyRates = NBPClient().getCurrencyExchangeRates("USD", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 4, 26))

    CsvCurrencyWriter.writeToFile(
        CsvExportInput(
            currencyCode = currencyRates.code,
            exchangeRates = currencyRates.rates.map { CsvExchangeRateRecord(it.effectiveDate, it.mid) }
        )
    )
}