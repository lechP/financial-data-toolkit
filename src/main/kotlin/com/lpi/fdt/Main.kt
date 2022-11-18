package com.lpi.fdt

import com.lpi.fdt.config.UploadConfig
import com.lpi.fdt.config.UploadCoordinates
import com.lpi.fdt.quotations.currencies.NBPClient
import com.lpi.fdt.quotations.stocks.StocksFacade
import com.lpi.fdt.quotations.stocks.StooqClient
import com.lpi.fdt.service.DefaultDataUploadService
import com.lpi.fdt.sheets.SpreadsheetCoordinates
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main(): Unit = runBlocking {

    val dataUploadService = DefaultDataUploadService(
        currencyClient = NBPClient(),
        stocksFacade = StocksFacade(StooqClient())
    )

    val config = UploadConfig()

    config.currencies.forEach { currencyConfig ->
        launch {
            dataUploadService.updateCurrencyRates(
                currencyConfig.symbol,
                currencyConfig.coordinates.toSpreadsheetCoordinates()
            )
        }
    }
    config.stocks.forEach { stockConfig ->
        launch {
            dataUploadService.updateStockQuotations(
                stockConfig.symbol,
                stockConfig.coordinates.toSpreadsheetCoordinates()
            )
        }
    }
}

fun UploadCoordinates.toSpreadsheetCoordinates() = SpreadsheetCoordinates(spreadsheetId, range)