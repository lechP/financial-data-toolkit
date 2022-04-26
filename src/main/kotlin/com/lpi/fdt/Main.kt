package com.lpi.fdt

import com.lpi.fdt.currencies.NBPClient
import java.time.LocalDate


fun main(args: Array<String>) {
    val currencyRates = NBPClient().getCurrencyRates("USD", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 4, 26))
    println(currencyRates)
}