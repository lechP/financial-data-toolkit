package com.lpi.fdt.experimental.htmlparser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class CitiHtmlTransactionParser : HtmlTransactionParser {
    override fun parseTransactions(html: String): List<BudgetTransaction> {
        val doc: Document = Jsoup.parse(html)

        val transactionElements = doc.select("div[data-index]")

        return transactionElements.map { transactionElement ->
            val dateElement = transactionElement.selectFirst("span[id^=transactionDate_]")
            val descriptionElement = transactionElement.selectFirst(".cbol-trans-desc")
            val amountElement = transactionElement.selectFirst(".format-amount-holder")

            val rawDate = dateElement!!.text()
            val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("pl", "PL"))
            val parsedDate = LocalDate.parse(rawDate, dateFormatter)
            val description = descriptionElement!!.text()
            val amount = BigDecimal(amountElement!!.text().replace(",", ".").replace(" ", ""))

            BudgetTransaction(parsedDate, amount, description)
        }.sortedBy { it.date }
    }

}

