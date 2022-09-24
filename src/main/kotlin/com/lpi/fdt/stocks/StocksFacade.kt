package com.lpi.fdt.stocks

import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.LocalDate

class StocksFacade(
    private val stocksClient: StocksClient
) {
    // TODO unit tests
    suspend fun getHistoricalValues(symbol: String): List<StockDailyRecord> =
        stocksClient.getValueHistory(symbol)
            .parseStockClientResponse()

    private fun String.parseStockClientResponse() =
        split("\r\n").drop(1).filter { it.isNotBlank() }.map { it.toStockDailyRecord() }

    private fun String.toStockDailyRecord(): StockDailyRecord {
        return split(",").let {
            StockDailyRecord(
                date = LocalDate.parse(it[0]),
                open = BigDecimal(it[1]),
                max = BigDecimal(it[2]),
                min = BigDecimal(it[3]),
                close = BigDecimal(it[4]),
                volume = if (it.size > 5) BigDecimal(it[5]) else null
            )
        }
    }
}

data class StockDailyRecord(
    val date: LocalDate,
    val open: BigDecimal,
    val max: BigDecimal,
    val min: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal?
)

fun main() {
    val client = StooqClient()
    val service = StocksFacade(client)

    runBlocking {
        println(service.getHistoricalValues("PKN"))
    }
}