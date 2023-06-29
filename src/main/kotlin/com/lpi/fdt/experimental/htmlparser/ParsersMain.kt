package com.lpi.fdt.experimental.htmlparser

import java.io.FileInputStream
import java.math.BigDecimal

fun main() {
    // filter
    val month = 6
    val day = 1


    // parse HTML file for Millennium
//    val htmlM = FileInputStream("input/transactionHistory.html").readBytes().decodeToString()
//    val transactionsM: List<BudgetTransaction> = MHtmlTransactionParser().parseTransactions(htmlM)
//    filterAndPrintTransactions(month,day,transactionsM)

    println()
    println("--------------------------------------------")
    println()

    // parse HTML file for Citi
    val htmlC = FileInputStream("input/citi.html").readBytes().decodeToString()
    val transactionsC: List<BudgetTransaction> = CitiHtmlTransactionParser().parseTransactions(htmlC)
    filterAndPrintTransactions(month, day, transactionsC)

    println()
    println("--------------------------------------------")
    println()

    // parse HTML file for PKO credit card
    val htmlP = FileInputStream("input/history_pko.html").readBytes().decodeToString()
    val transactionsP: List<BudgetTransaction> = PKOCreditCardHtmlTransactionParser().parseTransactions(htmlP)
    filterAndPrintTransactions(month, day, transactionsP)
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