package com.lpi.fdt.experimental.htmlparser

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.math.BigDecimal
import java.time.LocalDate

class MHtmlTransactionParser : HtmlTransactionParser {

    override fun parseTransactions(html: String): List<BudgetTransaction> =
        getTransactionsTable(html).map { it.parseBudgetTransaction() }

    private fun getTransactionsTable(html: String) =
        Jsoup.parse(html).select("table").first()!!.select("tbody").select("tr")

    private fun Element.parseBudgetTransaction(): BudgetTransaction {
        // read columns
        val transactionDate = LocalDate.parse(col(2))
        val description = col(7)

        val charge = col(8)
        val credit = col(9)

        val amount = when {
            charge.isNotEmpty() -> charge.toBigDecimal()
            credit.isNotEmpty() -> credit.toBigDecimal()
            else -> BigDecimal.ZERO
        }


        return BudgetTransaction(
            transactionDate,
            -amount,
            description.replace(',', ';')
        )
    }


    private fun Element.col(index: Int): String = select("td:nth-child($index)").text()
}

