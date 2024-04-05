package com.lpi.fdt.experimental.htmlparse.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class CitiHtmlTransactionParser(override val content: String) : HtmlTransactionParser {
    override fun getTransactions(): List<BudgetTransaction> =
        getTransactionElements(content)
            .mapNotNull { it.parseSingleTransaction() }
            .sortedBy { it.date }

    override fun name() = "CitiBank"

    private fun getTransactionElements(html: String) =
        Jsoup.parse(html).select("div[data-index]")

    private fun Element.parseSingleTransaction(): BudgetTransaction? {
        val dateElement = selectFirst("span[id^=transactionDate_]")
        val descriptionElement = selectFirst(".cbol-trans-desc")
        val amountElement = selectFirst(".format-amount-holder")

        val rawDate = dateElement?.text()
        val rawAmount = amountElement?.text()

        return if (rawDate != null && rawAmount != null) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("pl", "PL"))
            val description = descriptionElement?.text() ?: "-- DESCRIPTION NOT PROVIDED --"
            val amount = BigDecimal(rawAmount.replace(",", ".").replace(" ", ""))

            val parsedDate = LocalDate.parse(rawDate, dateFormatter)
            BudgetTransaction(parsedDate, -amount, description)
        } else null
    }

}

