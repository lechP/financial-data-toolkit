package com.lpi.fdt.experimental.htmlparser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
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

                BudgetTransaction(
                    transactionDate,
                    -amount,
                    description.replace(',', ';')
                )
            }
        }
        return transactions
    }

    private fun Element.col(index: Int): String = select("td:nth-child($index)").text()
}

