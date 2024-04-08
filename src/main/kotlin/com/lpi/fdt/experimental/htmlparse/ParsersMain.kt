package com.lpi.fdt.experimental.htmlparse

import com.lpi.fdt.config.loadProperties
import com.lpi.fdt.experimental.htmlparse.parser.BudgetTransaction
import java.io.File
import java.math.BigDecimal

fun main(args: Array<String>) {

    val month = if (args.isNotEmpty()) args[0].toInt() else 0
    val day = if (args.size > 1) args[1].toInt() else 0

    val dictionaries = DictionariesConfig()

    //print all files names within input directory
    val files = File("input").listFiles()
    println("Found following files to parse:")
    files?.forEach {
        println(it.name)
    }
    printltab()

    files?.forEach { file ->
        val content = file.readText()
        val parser = ParserFactory().createParser(content)
        if (parser != null) {
            println("Parsing [${file.name}] by ${parser.name()} parser...\n\n")
            filterAndPrintTransactions(month, day, parser.getTransactions(), dictionaries.shops)
        } else {
            println("No parser found for [${file.name}]")
        }
        printltab()
    }

}

private fun printltab() {
    println()
    println("--------------------------------------------")
    println()
}

fun filterAndPrintTransactions(
    month: Int,
    day: Int,
    transactions: List<BudgetTransaction>,
    shops: List<ShopDictionaryEntry>
) {
    val filteredTransactions =
        transactions.filter { it.date.monthValue >= month && it.date.dayOfMonth >= day }
            .filter { it.amount != BigDecimal.ZERO }

    // print in csv like format
    filteredTransactions.forEach {
        println("${it.date.dayOfMonth},${it.description},${it.amount},${it.description.deriveShop(shops)}")
    }
}

fun String.deriveShop(shops: List<ShopDictionaryEntry>): String =
    split("; ").first().let { fullShopName ->
        shops.firstOrNull { fullShopName.startsWith(it.nameStart) }?.targetName ?: fullShopName
    }

class DictionariesConfig {

    private val properties: Dictionaries by lazy {
        loadProperties("config/dictionaries.yaml")
    }

    val shops: List<ShopDictionaryEntry> by lazy {
        properties.shops
    }
}

data class Dictionaries(
    val shops: List<ShopDictionaryEntry>,
)

data class ShopDictionaryEntry(
    val nameStart: String,
    val targetName: String
)