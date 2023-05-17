package com.lpi.fdt.service

import com.lpi.fdt.quotations.currencies.NBPClient
import com.lpi.fdt.quotations.stocks.StocksFacade
import com.lpi.fdt.sheets.SpreadsheetCoordinates
import com.lpi.fdt.sheets.SpreadsheetService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate

interface DataUploadService {
    /**
     * currently supports only conversion of
     * @param currencySymbol to PLN
     */
    suspend fun updateCurrencyRates(currencySymbol: String, spreadsheetCoordinates: SpreadsheetCoordinates)

    /** currently it's assumed stock is quotated in PLN and quotation at the end of the day (close) is taken */
    suspend fun updateStockQuotations(stockSymbol: String, spreadsheetCoordinates: SpreadsheetCoordinates)

}

class DefaultDataUploadService(
    private val currencyClient: NBPClient,
    private val stocksFacade: StocksFacade,
) : DataUploadService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun updateCurrencyRates(currencySymbol: String, spreadsheetCoordinates: SpreadsheetCoordinates) {
        val lastDate = getLastDate(spreadsheetCoordinates)
        logger.info("Update $currencySymbol from $lastDate")
        // TODO check if range is > 0 | test "should not call if range is empty
        val dateFrom = lastDate.plusDays(1)
        val dateTo = LocalDate.now()
        if(dateFrom <= dateTo) {
            val currencyRates =
                currencyClient.getCurrencyExchangeRates(currencySymbol, lastDate.plusDays(1), LocalDate.now())
            val currencyInput = currencyRates.flatMap { it.rates }.map { listOf(it.date.toString(), it.value) }
            // write values
            SpreadsheetService.appendValues(spreadsheetCoordinates, currencyInput)
        } else {
            logger.info("Currency data is up to date")
        }
    }

    override suspend fun updateStockQuotations(stockSymbol: String, spreadsheetCoordinates: SpreadsheetCoordinates) {
        val lastDate = getLastDate(spreadsheetCoordinates)
        logger.info("Update $stockSymbol from $lastDate")
        // TODO ignore when lastDate==today
        val results = stocksFacade.getHistoricalValues(stockSymbol).filter { it.date > lastDate }
        val stocksInput = results.map { listOf(it.date.toString(), it.close) }
        SpreadsheetService.appendValues(spreadsheetCoordinates, stocksInput)
    }

    // TODO error handling
    private fun getLastDate(spreadsheetCoordinates: SpreadsheetCoordinates): LocalDate =
        LocalDate.parse(SpreadsheetService.getRangeValues(spreadsheetCoordinates).last()[0] as String)

}