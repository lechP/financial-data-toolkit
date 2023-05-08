package com.lpi.fdt.experimental.htmlparser

import java.math.BigDecimal
import java.time.LocalDate

data class BudgetTransaction(
    val date: LocalDate,
    val amount: BigDecimal,
    val description: String
)

interface HtmlTransactionParser {

    fun parseTransactions(html: String): List<BudgetTransaction>

}