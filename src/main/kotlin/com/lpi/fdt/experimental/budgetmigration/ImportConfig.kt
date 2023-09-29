package com.lpi.fdt.experimental.budgetmigration

import com.lpi.fdt.config.loadProperties

class BudgetImportConfig {

    private val properties: ImportProperties by lazy {
        loadProperties("dataimport.yaml")
    }

    val spreadsheetId: String by lazy {
        properties.budgetSpreadsheetId
    }
}

data class ImportProperties(
    val budgetSpreadsheetId: String,
)

