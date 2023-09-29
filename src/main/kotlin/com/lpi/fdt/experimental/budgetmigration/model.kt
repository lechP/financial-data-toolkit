package com.lpi.fdt.experimental.budgetmigration

import java.math.BigDecimal
import java.time.LocalDate

/** old model (V1) which does not cover foreign currencies */

data class SpreadsheetExpenseV1(
    val date: LocalDate,
    val description: String,
    val amount: BigDecimal,
    val sourceAccountId: String,
    val category: String,
    val subcategory: String?,
    val receiver: String,
)

data class SpreadsheetTransferV1(
    val date: LocalDate,
    val sourceAccountId: String,
    val destinationAccountId: String,
    val amount: BigDecimal,
    val description: String,
)

data class SpreadsheetIncomeV1(
    val date: LocalDate,
    val description: String,
    val amount: BigDecimal,
    val destinationAccountId: String,
    val category: String
)