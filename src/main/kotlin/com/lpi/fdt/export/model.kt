package com.lpi.fdt.export

import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvDate
import com.opencsv.bean.CsvNumber
import com.opencsv.bean.StatefulBeanToCsvBuilder
import java.io.FileWriter
import java.io.Writer
import java.time.LocalDate

object CsvCurrencyWriter {
    fun writeToFile(currencyCode: String, input: List<CsvCurrencyRecord>) {
        val writer: Writer = FileWriter("PLNto$currencyCode.csv")
        val beanToCsv = StatefulBeanToCsvBuilder<CsvCurrencyRecord>(writer).withApplyQuotesToAll(false).build()
        beanToCsv.write(input)
        writer.close()
    }
}

data class CsvCurrencyRecord(
    @CsvBindByName(column = "Effective Date")
    @CsvDate("yyyy-MM-dd")
    val effectiveDate: LocalDate,
    @CsvBindByName(column = "Exchange Rate")
    @CsvNumber("#0.0000")
    val exchangeRate: Double // TODO BigDecimal
)