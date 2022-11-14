package com.lpi.fdt.config

import java.io.FileInputStream
import java.util.*

class Config {

    private val properties: Properties by lazy {
        val props = Properties()
        props.load(FileInputStream("application.properties"))
        props
    }

    val googleCredentials: GoogleCredentials by lazy {
        GoogleCredentials(
            properties.getProperty("google.clientId"),
            properties.getProperty("google.clientSecret"),
        )
    }

    val currenciesSpreadsheetId: String by lazy {
        properties.getProperty("currenciesSpreadsheetId")
    }

    val stocksSpreadsheetId: String by lazy {
        properties.getProperty("stocksSpreadsheetId")
    }

    // ultimately should be an array
    val stockSpreadsheetData: StockSpreadsheetData by lazy {
        StockSpreadsheetData(
            symbol = properties.getProperty("stock.symbol"),
            spreadsheetRange = properties.getProperty("stock.range"),
        )
    }
}

data class StockSpreadsheetData(
    val symbol: String,
    val spreadsheetRange: String
)

data class GoogleCredentials(
    val clientId: String,
    val clientSecret: String
)