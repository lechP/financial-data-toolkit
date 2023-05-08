package com.lpi.fdt.experimental.htmlparser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.FileInputStream
import java.math.BigDecimal
import java.time.LocalDate

class MHtmlTransactionParser : HtmlTransactionParser {
    override fun parseTransactions(html: String): List<BudgetTransaction> {
        val doc: Document = Jsoup.parse(html)
        val table = doc.select("table").first()

        val transactions: List<BudgetTransaction> = table!!.select("tr").mapNotNull { row ->
            // skip header - WARN! By default, M header is `td` - has to be fixed
            if (row.select("th").isNotEmpty()) {
                null
            } else {

                // read not empty columns
                val transactionDate = LocalDate.parse(row.col(2))
                val description = row.col(7)
                // TODO 8 to obciazenia, 9 to uznania
                val amount = row.col(8).let {
                    if (it.isNotEmpty()) {
                        it.toBigDecimal()
                    } else BigDecimal.ZERO
                }
                println("$description | $amount")

                BudgetTransaction(
                    transactionDate,
                    -amount,
                    description.replace(',', ';')
                )
            }
        }
        return transactions
    }

}

fun main() {
    // read HTML file
    val html = FileInputStream("input/transactionHistory.html").readBytes().decodeToString()

    // filter
    val month = 4
    val day = 1

    // iterate over table rows and map them
    val transactions: List<BudgetTransaction> = MHtmlTransactionParser().parseTransactions(html)

    val filteredTransactions =
        transactions.filter { it.date.monthValue >= month && it.date.dayOfMonth >= day }.filter { it.amount != BigDecimal.ZERO }

    // TODO derive shop like ALDI, JMP S.A. BIEDRONKA...
    // what could be more generic name for shop or person who is involved in transaction?


    // print in csv like format
    filteredTransactions.forEach {
        println("${it.date.dayOfMonth},${it.description},${it.amount},${it.description.deriveShop()}")
    }


}

fun String.deriveShop() = split("; ").first().let {
    when {
        it == "Allegro" -> "allegro.pl"
        it.startsWith("LOTOS") -> "Lotos"
        it.startsWith("ZABKA") -> "Żabka"
        it.startsWith("ZAPPKA PAY") -> "Żabka"
        it.startsWith("LIDL") -> "Lidl"
        it.startsWith("JMP S.A. BIEDRONKA") -> "Biedronka"
        it.startsWith("ALDI SP. Z O.O.") -> "Aldi"
        it.startsWith("AUCHAN POLSKA") -> "Auchan"
        it == "KAUFLAND" -> "Kaufland"
        it == "STOKROTKA" -> "Stokrotka"
        it == "SKLEP POD DEBEM" -> "Pod Dębem"
        else -> it
    }
}

fun Element.col(index: Int): String = select("td:nth-child($index)").text()
