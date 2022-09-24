package com.lpi.fdt.export

import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvDate
import com.opencsv.bean.CsvNumber
import com.opencsv.bean.StatefulBeanToCsvBuilder
import java.io.FileWriter
import java.io.Writer
import java.math.BigDecimal
import java.time.LocalDate

object CsvCurrencyWriter {
    fun writeToFile(input: CsvExportInput): String {
        val filename = "PLNto${input.currencyCode}.csv"
        val writer: Writer = FileWriter(filename)
        val beanToCsv = StatefulBeanToCsvBuilder<CsvExchangeRateRecord>(writer).withApplyQuotesToAll(false).build()
        beanToCsv.write(input.exchangeRates)
        writer.close()
        return filename
    }
}

data class CsvExportInput(
    val currencyCode: String,
    val exchangeRates: List<CsvExchangeRateRecord>
)

data class CsvExchangeRateRecord(
    @CsvBindByName(column = "Effective Date")
    @CsvDate("yyyy-MM-dd")
    val effectiveDate: LocalDate,
    @CsvBindByName(column = "Exchange Rate")
    @CsvNumber("#0.0000")
    val exchangeRate: BigDecimal
)