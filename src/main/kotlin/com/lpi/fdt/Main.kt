package com.lpi.fdt

import com.lpi.fdt.config.Config
import com.lpi.fdt.quotations.currencies.NBPClient
import com.lpi.fdt.export.CsvExchangeRateRecord
import com.lpi.fdt.export.CsvCurrencyWriter
import com.lpi.fdt.export.CsvExportInput
import com.lpi.fdt.quotations.stocks.StocksFacade
import com.lpi.fdt.quotations.stocks.StooqClient
import com.lpi.fdt.service.DefaultDataUploadService
import com.lpi.fdt.sheets.SpreadsheetCoordinates
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate


fun main(): Unit = runBlocking {

    val dataUploadService = DefaultDataUploadService(
        currencyClient = NBPClient(),
        stocksFacade = StocksFacade(StooqClient())
    )

    // read data from config
    val config = Config()
    val usd = "USD"
    val usdCoordinates = SpreadsheetCoordinates(config.currenciesSpreadsheetId, "${usd}toPLN")
    val eur = "EUR"
    val eurCoordinates = SpreadsheetCoordinates(config.currenciesSpreadsheetId, "${eur}toPLN")
    val stockCoordinates =
        SpreadsheetCoordinates(config.stocksSpreadsheetId, config.stockSpreadsheetData.spreadsheetRange)

    launch {
        dataUploadService.updateCurrencyRates(usd, usdCoordinates)
        dataUploadService.updateCurrencyRates(eur, eurCoordinates)
        dataUploadService.updateStockQuotations(config.stockSpreadsheetData.symbol, stockCoordinates)
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