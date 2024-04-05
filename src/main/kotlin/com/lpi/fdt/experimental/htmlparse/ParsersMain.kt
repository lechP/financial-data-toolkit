package com.lpi.fdt.experimental.htmlparse

import com.lpi.fdt.experimental.htmlparse.parser.BudgetTransaction
import java.io.File
import java.math.BigDecimal

fun main(args: Array<String>) {

    val month = if (args.isNotEmpty()) args[0].toInt() else 0
    val day = if (args.size > 1) args[1].toInt() else 0

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
            filterAndPrintTransactions(month, day, parser.getTransactions())
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

fun filterAndPrintTransactions(month: Int, day: Int, transactions: List<BudgetTransaction>) {
    val filteredTransactions =
        transactions.filter { it.date.monthValue >= month && it.date.dayOfMonth >= day }
            .filter { it.amount != BigDecimal.ZERO }

    // print in csv like format
    filteredTransactions.forEach {
        println("${it.date.dayOfMonth},${it.description},${it.amount},${it.description.deriveShop()}")
    }
}


fun String.deriveShop() = split("; ").first().let {
    when {
        it == "Allegro" -> "allegro.pl"
        it.startsWith("LOTOS") -> "Lotos"
        it.startsWith("ZABKA") -> "Żabka"
        it.startsWith("ZAPPKA PAY") -> "Żabka"
        it.startsWith("LIDL") -> "Lidl"
        it.startsWith("JMP S.A. BIEDRONKA") -> "Biedronka"
        it.startsWith("ALDI SP. Z O.O.") -> "Aldi"
        it.startsWith("AUCHAN POLSKA") -> "Auchan"
        it.startsWith("KAUFLAND") -> "Kaufland"
        it.startsWith("STOKROTKA") -> "Stokrotka"
        it.startsWith("SKLEP POD DEBEM") -> "Pod Dębem"
        it.startsWith("ROSSMANN") -> "Rossmann"
        it.startsWith("PIEKARNIA HERT") -> "Hert"
        it.startsWith("PEPCO") -> "Pepco"
        else -> it
    }
}