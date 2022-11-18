package com.lpi.fdt.service

interface DataUploadService {
    /**
     * currently supports only conversion of
     * @param currencySymbol to PLN
     */
    fun updateCurrencyRates(currencySymbol: String, config: SpreadsheetUploadConfig)

    /** currently it's assumed stock is quotated in PLN and quotation at the end of the day (close) is taken */
    fun updateStockQuotations(stockSymbol: String, config: SpreadsheetUploadConfig)

}

class DefaultDataUploadService(

) : DataUploadService {
    override fun updateCurrencyRates(currencySymbol: String, config: SpreadsheetUploadConfig) {
        TODO("Not yet implemented")
    }

    override fun updateStockQuotations(stockSymbol: String, config: SpreadsheetUploadConfig) {
        TODO("Not yet implemented")
    }
}

data class SpreadsheetUploadConfig(
    val spreadsheetId: String,
    val range: String,
)