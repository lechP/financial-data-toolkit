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
import java.io.File
import java.time.LocalDate

private const val tokensDirectoryPath = "tokens"

private const val applicationName = "Financial Data Toolkit"

object SpreadsheetService {

    // TODO DI
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

fun main() {
    val spreadsheetId = Config().demoSpreadsheetId
    val range = "USDtoPLN"

    val response = SpreadsheetService.instance().spreadsheets().values()[spreadsheetId, range]
        .execute()
    val values = response.getValues()
    if (values == null || values.isEmpty()) {
        println("No data found.")
    } else {
        println("description, amount")
        values.forEach { row ->
            println("${row[0]}, ${row[1]}")
        }
    }

    // write example
    val range2 = "fx!B193"
    val input = listOf(listOf(LocalDate.now().toString(), 4.9000))
    val body: ValueRange = ValueRange().setValues(input)
    val result = SpreadsheetService.instance().spreadsheets().values().append(spreadsheetId, range, body)
        .setValueInputOption("USER_ENTERED")
        .execute()

    println("${result.tableRange} updated")

}

/*
TODO
 I. CURRENCIES
    I want:
    1. take last record from spreadsheet and check the DateN
    2. get fx data for date range [DateN, today()]
    3. append the data to the spreadsheet
    repeat the operation for USD and EUR
 II. STOCKS
    Actually I want the same thing
 */