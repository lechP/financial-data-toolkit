package com.lpi.fdt.experimental.htmlparse.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File
import java.time.LocalDate

class PKOAccountHtmlTransactionParser(override val content: String) : HtmlTransactionParser {


    // extract list of BudgetTransaction from html content
    override fun getTransactions(): List<BudgetTransaction> {
        val table = getTransactionsTable(content)
        return table.map { it.parseBudgetTransaction() }.sortedBy { it.date }
    }

    // extract table from html content
    private fun getTransactionsTable(html: String) =
        Jsoup.parse(html).select("table#lista_transakcji")
            .first()!!.select("tbody")
            .select("tr")
            .drop(1) // skip header row

    // extract BudgetTransaction from table row
    private fun Element.parseBudgetTransaction(): BudgetTransaction {
        val transactionDate = LocalDate.parse(col(1))
        val description = col(4)
        val amount = col(5).toBigDecimal()
        return BudgetTransaction(transactionDate, amount, description)
    }

    private fun Element.col(index: Int): String = select("td:nth-child($index)").text()

    override fun name() = "PKO Account"
}

fun main() {
    val parser = PKOAccountHtmlTransactionParser(File("input/zestawienie_pko.html").readText())
    val transactions = parser.getTransactions()
    transactions.forEach { println(it) }

}