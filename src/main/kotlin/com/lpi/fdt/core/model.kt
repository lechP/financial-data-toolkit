package com.lpi.fdt.core

import java.math.BigDecimal
import java.time.LocalDate

data class CurrencyExchangeRates(
    val from: String,
    val to: String,
    val quotations: List<Quotation>,
)

data class StockValues(
    val symbol: String,
    val currency: String,
    val quotations: List<Quotation>,
)

data class Quotation(
    val value: BigDecimal,
    val date: LocalDate
)