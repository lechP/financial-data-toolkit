package com.lpi.fdt.experimental.htmlparser

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File
import java.time.LocalDate

class PKOCreditCardHtmlTransactionParser(override val content: String) : HtmlTransactionParser {

    override fun parseTransactions(html: String): List<BudgetTransaction> =
        html.extractTransactionsTable().getTransactionRecords().map { it.parseBudgetTransaction() }.reversed()

    private fun String.extractTransactionsTable(): String {
        val lines = split("\n")
        val tableStartingIndex = lines.indexOfLast { it.contains("<table>") }
        val tableEndingIndex = lines.indexOfFirst { it.contains("</table>") }
        return lines.subList(tableStartingIndex, tableEndingIndex + 1).joinToString("\n")
    }

    private fun String.getTransactionRecords() = Jsoup.parse(this).select("tr").drop(1)

    private fun Element.parseBudgetTransaction(): BudgetTransaction {
        // read columns
        val transactionDate = LocalDate.parse(col(1))
        val description = col(3).replace("Tytuł : ", "").replace("\n", "").trim()
        val amount = col(4).replace(" PLN", "").toBigDecimal()

        return BudgetTransaction(
            transactionDate,
            -amount,
            description.replace(',', ';')
        )
    }

    private fun Element.col(index: Int): String = select("td")[index].text()
}

