package com.lpi.fdt.sheets

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private const val applicationName = "financial-data-toolkit"

object SpreadsheetService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val credentials = GoogleCredentials.getApplicationDefault()
        .createScoped(listOf(SheetsScopes.SPREADSHEETS))

    // TODO DI instead of instance
    fun instance(): Sheets {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        return Sheets.Builder(
            httpTransport,
            GsonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(credentials),
        )
            .setApplicationName(applicationName)
            .build()
    }

    fun getRangeValues(coordinates: SpreadsheetCoordinates): List<List<Any>> =
            getReadRequest(coordinates).execute().getValues()


    private fun getReadRequest(coordinates: SpreadsheetCoordinates): Sheets.Spreadsheets.Values.Get =
        instance().spreadsheets().values()[coordinates.spreadsheetId, coordinates.range]

    fun appendValues(coordinates: SpreadsheetCoordinates, values: List<List<Any>>) {
        if(values.isNotEmpty()) {
            val body: ValueRange = ValueRange().setValues(values)
            val googleResponse = getAppendRequest(coordinates, body).execute()
            logger.info("${googleResponse.updates.updatedRows} rows updated")
        } else {
            logger.info("No values to append. Update request ignored.")
        }
    }

    private fun getAppendRequest(
        coordinates: SpreadsheetCoordinates,
        body: ValueRange
    ): Sheets.Spreadsheets.Values.Append =
        instance().spreadsheets().values().append(coordinates.spreadsheetId, coordinates.range, body)
            .setValueInputOption("USER_ENTERED")
}

data class SpreadsheetCoordinates(
    val spreadsheetId: String,
    val range: String,
)