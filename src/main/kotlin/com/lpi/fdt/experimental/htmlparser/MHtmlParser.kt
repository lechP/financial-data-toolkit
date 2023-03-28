package com.lpi.fdt.experimental.htmlparser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.FileInputStream
import java.math.BigDecimal
import java.time.LocalDate

fun main() {
    // read HTML file
    val html = FileInputStream("input/transactionHistory.html").readBytes().decodeToString()

    // filter
    val month = 3
    val day = 7

    val doc: Document = Jsoup.parse(html)
    val table = doc.select("table").first()

    // iterate over table rows and map them
    val transactions: List<BudgetTransaction> = table!!.select("tr").mapNotNull { row ->
        // skip header - WARN! By default, M header is `td` - has to be fixed
        if (row.select("th").isNotEmpty()) {
            null
        } else {

            // read not empty columns
            val cardAccountNumber = row.col(1)
            val transactionDate = LocalDate.parse(row.col(2))
            val settlementDate = LocalDate.parse(row.col(3))
            val description = row.col(7)
            val amount = row.col(8).let {
                if (it.isNotEmpty()) {
                    it.toBigDecimal()
                } else BigDecimal.ZERO
            }
            val currency = row.col(11)

            BudgetTransaction(transactionDate.dayOfMonth, transactionDate.monthValue, -amount, description.replace(',', ';'))
        }
    }

    val filteredTransactions = transactions.filter { it.month >= month && it.day >= day }.filter { it.amount != BigDecimal.ZERO }

    // TODO derive shop like ALDI, JMP S.A. BIEDRONKA...

    // print in csv like format
    filteredTransactions.forEach {
        println("${it.day},${it.description},${it.amount}")
    }


}

fun Element.col(index: Int): String = select("td:nth-child($index)").text()

data class BudgetTransaction(
    val day: Int,
    val month: Int,
    val amount: BigDecimal,
    val description: String
)
