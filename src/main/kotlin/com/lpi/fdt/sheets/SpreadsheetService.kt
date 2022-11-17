package com.lpi.fdt.sheets

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.lpi.fdt.config.Config
import com.lpi.fdt.quotations.currencies.NBPClient
import com.lpi.fdt.quotations.stocks.StocksFacade
import com.lpi.fdt.quotations.stocks.StooqClient
import java.io.File
import java.time.LocalDate

private const val tokensDirectoryPath = "tokens"

private const val applicationName = "Financial Data Toolkit"

object SpreadsheetService {

    private val config = Config()

    /**
     * Global instance of the required scopes.
     * If modifying these scopes, delete previously saved tokens/ folder.
     */
    private val authScopes = listOf(SheetsScopes.SPREADSHEETS)

    private fun getCredentials(transport: HttpTransport): Credential {
        val flow = GoogleAuthorizationCodeFlow.Builder(
            transport,
            GsonFactory.getDefaultInstance(),
            config.googleCredentials.clientId,
            config.googleCredentials.clientSecret,
            authScopes
        )
            .setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    fun instance(): Sheets {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        return Sheets.Builder(
            httpTransport,
            GsonFactory.getDefaultInstance(),
            getCredentials(httpTransport)
        )
            .setApplicationName(applicationName)
            .build()
    }

}

suspend fun main() {
    updateCurrencyRates("USD")
    updateCurrencyRates("EUR")

    updateStockQuotations()

}

suspend fun updateStockQuotations() {
    val spreadsheetId = Config().stocksSpreadsheetId
    // for now single quotation; ultimately should iterate over array taken from props
    val stockData = Config().stockSpreadsheetData
    val lastDate = getLastDate(spreadsheetId, stockData.spreadsheetRange)

    val stocksFacade = StocksFacade(StooqClient())
    val results = stocksFacade.getHistoricalValues(stockData.symbol).filter { it.date > lastDate }
    val stocksInput = results.map { listOf(it.date.toString(), it.close)}
    appendValues(spreadsheetId, stockData.spreadsheetRange, stocksInput)
}

/**
    1. take last record from spreadsheet and check the DateN
    2. get fx data for date range [DateN, today()]
    3. append the data to the spreadsheet
 */
suspend fun updateCurrencyRates(symbol: String) {
    val spreadsheetId = Config().currenciesSpreadsheetId
    val range = "${symbol}toPLN"
    val lastDate = getLastDate(spreadsheetId, range)
    // TODO check if range is > 0 | test "should not call if range is empty
    val currencyRates = NBPClient().getCurrencyExchangeRates(symbol, lastDate.plusDays(1), LocalDate.now())
    val currencyInput = currencyRates.rates.map { listOf(it.effectiveDate.toString(), it.mid)}
    // write values
    appendValues(spreadsheetId, range, currencyInput)
}

fun appendValues(spreadsheetId: String, range: String, values: List<List<Any>>) {
    val body: ValueRange = ValueRange().setValues(values)
    val result = SpreadsheetService.instance().spreadsheets().values().append(spreadsheetId, range, body)
        .setValueInputOption("USER_ENTERED")
        .execute()
    // TODO logger
    println("${result.updates.updatedRows} rows updated")
}

fun getLastDate(spreadsheetId: String, range: String): LocalDate =
    LocalDate.parse(getRangeValues(spreadsheetId, range).last()[0] as String)

fun getRangeValues(spreadsheetId: String, range: String): List<List<Any>> =
    SpreadsheetService.instance().spreadsheets().values()[spreadsheetId, range]
        .execute().getValues()