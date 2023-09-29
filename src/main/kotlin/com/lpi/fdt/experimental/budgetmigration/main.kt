package com.lpi.fdt.experimental.budgetmigration

import com.lpi.fdt.sheets.SpreadsheetCoordinates
import com.lpi.fdt.sheets.SpreadsheetService
import java.time.LocalDate


fun main() {

    val spreadsheetId = BudgetImportConfig().spreadsheetId

    fun coords(sheetName: String, cols: String) = SpreadsheetCoordinates(
        spreadsheetId = spreadsheetId,
        range = "$sheetName!$cols"
    )

    val expenses = SpreadsheetService.getRangeValues(
        coords("1807","A:H")
    )

    val transfers = SpreadsheetService.getRangeValues(
        coords("1807","K:O")
    )

    val incomes = SpreadsheetService.getRangeValues(
        coords("1807", "Q:U")
    )

    println("expenses")
    println(expenses.drop(2))
    println("transfers")
    println(transfers.drop(2))
    println("incomes")
    println(incomes.drop(2))
}

data class SpreadsheetExpenseV1(
    val date: LocalDate,
    val description: String,

    )