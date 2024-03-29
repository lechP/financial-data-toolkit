package com.lpi.fdt.experimental.htmlparser

import java.math.BigDecimal
import java.time.LocalDate


data class BudgetTransaction(
    val date: LocalDate,
    val amount: BigDecimal,
    val description: String
    // TODO what could be a generic name for shop or person who is involved in transaction?
)

interface HtmlTransactionParser {

    fun parseTransactions(html: String): List<BudgetTransaction>

}