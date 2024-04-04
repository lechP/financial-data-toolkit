package com.lpi.fdt.experimental.htmlparser

import java.io.File
import java.io.FileInputStream
import java.math.BigDecimal

fun main(args: Array<String>) {

    val month = if(args.isNotEmpty()) args[0].toInt() else 0
    val day = if(args.size > 1) args[1].toInt() else 0

    //print all files names within input directory
    val files = File("input").listFiles()
    println("Found following files to parse:")
    files?.forEach {
        println(it.name)
    }
    printltab()

    // define "fingerprint" for each file and parse it depending on the fingerprint
    // TODO use strategy

    val pkoFingerprint = listOf("PKOInteligo", "<h3>Operacje zrealizowane</h3>", "<tr><td>Numer karty</td>")
    val milleFingerprint = listOf("<table border=\"1\">", "<td align=\"right\">Numer rachunku/karty</td>", "</table><br></body>")
    val citiFingerprint = listOf("tbd")

    files?.forEach { file ->
        val content = file.readText()
        when {
            pkoFingerprint.all { content.contains(it) } -> {
                println("Parsing [${file.name}] as PKO file\n\n")
                val transactions: List<BudgetTransaction> = PKOCreditCardHtmlTransactionParser().parseTransactions(content)
                filterAndPrintTransactions(month, day, transactions)
            }
            milleFingerprint.all { content.contains(it) } -> {
                println("Parsing [${file.name}] as Millennium file\n\n")
                val transactions: List<BudgetTransaction> = MHtmlTransactionParser().parseTransactions(content)
                filterAndPrintTransactions(month, day, transactions)
            }
            citiFingerprint.all { content.contains(it) } -> {
                println("Parsing [${file.name}] as Citi file\n\n")
                val transactions: List<BudgetTransaction> = CitiHtmlTransactionParser().parseTransactions(content)
                filterAndPrintTransactions(month, day, transactions)
            }
            else -> println("Unknown file type: ${file.name}")
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