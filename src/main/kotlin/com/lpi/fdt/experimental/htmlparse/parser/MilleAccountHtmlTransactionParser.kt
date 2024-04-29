package com.lpi.fdt.experimental.htmlparse.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.math.BigDecimal
import java.time.LocalDate

class MilleAccountHtmlTransactionParser(override val content: String) : HtmlTransactionParser {

    override fun getTransactions(): List<BudgetTransaction> =
        getTransactionsTable(content).map { it.parseBudgetTransaction() }.sortedBy { it.date }

    override fun name() = "Millenium Bank Account"

    private fun getTransactionsTable(html: String) =
        Jsoup.parse(html).select("table").first()!!.select("tbody").select("tr")

    private fun Element.parseBudgetTransaction(): BudgetTransaction {
        // read columns
        val description = col(7)

        // if descriptions ends with date in yyyy-mm-dd format then use it as transactionDate, otherwise use the one from column 2
        val dateRegex = """\d{4}-\d{2}-\d{2}""".toRegex()
        val date = dateRegex.find(description)?.value
        val transactionDate = if (date != null) LocalDate.parse(date) else LocalDate.parse(col(2))

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

