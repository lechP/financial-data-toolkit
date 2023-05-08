package com.lpi.fdt.experimental.htmlparser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class Transaction(val date: String, val description: String, val amount: Double, val currency: String)

fun main() {
    val htmlFile = File("input/citi.html")
    val document: Document = Jsoup.parse(htmlFile, "UTF-8")

    val transactionElements = document.select("div[data-index]")
    val transactions = mutableListOf<Transaction>()

    transactionElements.forEach { transactionElement ->
        val dateElement = transactionElement.selectFirst("span[id^=transactionDate_]")
        val descriptionElement = transactionElement.selectFirst(".cbol-trans-desc")
        val amountElement = transactionElement.selectFirst(".format-amount-holder")
        val currencyElement = transactionElement.selectFirst(".format-currency-holder")

        val rawDate = dateElement!!.text()
        val parsedDate = SimpleDateFormat("dd MMM yyyy", Locale("pl", "PL")).parse(rawDate)
        val formattedDate = SimpleDateFormat("dd.MM.yyyy").format(parsedDate)
        val description = descriptionElement!!.text()
        val amount = amountElement!!.text().replace(",", ".").replace(" ", "").toDouble()
        val currency = currencyElement!!.text()

        transactions.add(Transaction(formattedDate, description, amount, currency))
    }

    transactions.forEachIndexed { index, transaction ->
        println("Transakcja ${index + 1}:")
        println("Data transakcji: ${transaction.date}")
        println("Opis: ${transaction.description}")
        println("Kwota: ${transaction.amount} ${transaction.currency}")
        println()
    }

    transactions.reversed().forEach { transaction ->
        println("${transaction.date.split(".")[0]}, ${transaction.description}, ${-transaction.amount}")
    }
}

