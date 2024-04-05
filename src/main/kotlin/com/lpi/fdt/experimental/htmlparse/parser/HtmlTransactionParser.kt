package com.lpi.fdt.experimental.htmlparse.parser

import java.math.BigDecimal
import java.time.LocalDate


data class BudgetTransaction(
    val date: LocalDate,
    val amount: BigDecimal,
    val description: String
    // TODO what could be a generic name for shop or person who is involved in transaction?
)

interface HtmlTransactionParser {

    val content: String
    fun getTransactions(): List<BudgetTransaction>

}