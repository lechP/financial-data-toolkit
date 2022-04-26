package com.lpi.fdt

import com.lpi.fdt.currencies.NBPClient
import com.lpi.fdt.export.CsvCurrencyRecord
import com.lpi.fdt.export.CsvCurrencyWriter
import java.time.LocalDate


fun main() {
    val currencyRates = NBPClient().getCurrencyRates("USD", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 4, 26))

    val toExport = currencyRates.rates.map { CsvCurrencyRecord(it.effectiveDate, it.mid) }

    CsvCurrencyWriter.writeToFile(currencyRates.code, toExport)

}