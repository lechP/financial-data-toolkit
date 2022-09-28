package com.lpi.fdt.export

import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvDate
import com.opencsv.bean.CsvNumber
import com.opencsv.bean.StatefulBeanToCsvBuilder
import java.io.FileWriter
import java.io.Writer
import java.math.BigDecimal
import java.time.LocalDate

object CsvStocksWriter {
    fun writeToFile(input: StocksCsvWriterInput): String {
        val filename = "${input.symbol}.csv"
        val writer: Writer = FileWriter(filename)
        val beanToCsv = StatefulBeanToCsvBuilder<CsvStockPriceRecord>(writer).withApplyQuotesToAll(false).build()
        beanToCsv.write(input.valuations)
        writer.close()
        return filename
    }
}

data class StocksCsvWriterInput(
    val symbol: String,
    val valuations: List<CsvStockPriceRecord>
)


data class CsvStockPriceRecord(
    @CsvBindByName(column = "Effective Date")
    @CsvDate("yyyy-MM-dd")
    val effectiveDate: LocalDate,
    @CsvBindByName(column = "Price")
    @CsvNumber("#0.0000")
    val price: BigDecimal
)