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

    // TODO DI instead of instance
    private fun instance(): Sheets {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        return Sheets.Builder(
            httpTransport,
            GsonFactory.getDefaultInstance(),
            getCredentials(httpTransport)
        )
            .setApplicationName(applicationName)
            .build()
    }

    fun getRangeValues(coordinates: SpreadsheetCoordinates): List<List<Any>> =
        instance().spreadsheets().values()[coordinates.spreadsheetId, coordinates.range].execute().getValues()

    fun appendValues(coordinates: SpreadsheetCoordinates, values: List<List<Any>>) {
        val body: ValueRange = ValueRange().setValues(values)
        val result = instance().spreadsheets().values().append(coordinates.spreadsheetId, coordinates.range, body)
            .setValueInputOption("USER_ENTERED")
            .execute()
        // TODO logger
        println("${result.updates.updatedRows} rows updated")
    }

}

data class SpreadsheetCoordinates(
    val spreadsheetId: String,
    val range: String,
)