package com.lpi.fdt.experimental.htmlparse

import com.lpi.fdt.experimental.htmlparse.parser.BudgetTransaction
import com.lpi.fdt.experimental.htmlparse.parser.CitiHtmlTransactionParser
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class CitiHtmlTransactionParserTest {

    @Test
    fun `should parse single transaction`() {
        val html = """
            <div class="cbol-account-grid-body custom-data-grid-row-identifier   col-lg-12 col-md-12 col-sm-12 col-xs-12" data-index="5" data-pivot="95383823084122426569797">
            
                <div class="col-lg-2 col-md-2 col-sm-2 col-xs-12 cbol-grid-cell">
                    <div>
                        <div class="mobile-inline-header "><span id="date_root">Data</span></div>
                        <div class="ui-float-left ">    <span id="transactionDate_38_date">25 mar 2023</span>
                        </div><div class="ui-float-right desktop-hide-content text-right ui-font-bold-group"><span class="format-amount-holder negative-number"><span>-35</span><span>,50</span></span><span class="format-currency-holder negative-number" style="padding-left: 3px; word-break: normal; word-wrap: normal">PLN</span></div>
                    </div>
                </div>
                <div class="col-lg-7 col-md-7 col-sm-7 col-xs-12 cbol-grid-cell">
                    <div>
                        <div class="mobile-inline-header "><span id="description_root">Opis transakcji</span></div>
                        <div class="cbol-tj-tbl-content"><div class="cbol-trans-type cbol-trans-link cbol-active-link colored">GENERIC SHOP 1     WROCLAW      PL</div><div class="cbol-trans-desc cbol-trans-link cbol-active-link  ui-mgn-xs-top">GENERIC SHOP 1     WROCLAW      PL</div></div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-3 col-sm-3 col-xs-12 cbol-desktop-right-text  mobile-hide-content cbol-grid-cell">
                    <div>
                        <div class="mobile-inline-header show-inline-header"><span id="amount_root">Kwota</span></div>
                        <div class="ui-font-bold-group"><span class="format-amount-holder negative-number"><span>-87</span><span>,80</span></span><span class="format-currency-holder negative-number" style="padding-left: 3px; word-break: normal; word-wrap: normal">PLN</span></div>
                    </div>
                </div>
            </div>
        """.trimIndent()

        val transactions = CitiHtmlTransactionParser(html).getTransactions()

        val expectedTransaction = BudgetTransaction(
            date = LocalDate.of(2023, 3, 25),
            amount = BigDecimal("35.50"),
            description = "GENERIC SHOP 1 WROCLAW PL"
        )
        assertEquals(listOf(expectedTransaction), transactions)
    }

    @Test
    fun `should parse multiple transactions`() {
        val html = """
            <div class="cbol-account-grid-body custom-data-grid-row-identifier   col-lg-12 col-md-12 col-sm-12 col-xs-12" data-index="38">
                ...
                <span id="transactionDate_38_date">25 mar 2023</span>
                ...
                <div class="cbol-trans-desc">GENERIC SHOP 1    WROCLAW      PL</div>
                ...
                <span class="format-amount-holder">-35,50</span>
                ...
            </div>
            <div class="cbol-account-grid-body custom-data-grid-row-identifier   col-lg-12 col-md-12 col-sm-12 col-xs-12" data-index="39">
                ...
                <span id="transactionDate_39_date">26 mar 2023</span>
                ...
                <div class="cbol-trans-desc">OTHER SHOP    WROCLAW      PL</div>
                ...
                <span class="format-amount-holder">-120,45</span>
                ...
            </div>
        """.trimIndent()

        val transactions = CitiHtmlTransactionParser(html).getTransactions()

        val expectedTransaction1 = BudgetTransaction(
            date = LocalDate.of(2023, 3, 25),
            amount = BigDecimal("35.50"),
            description = "GENERIC SHOP 1 WROCLAW PL"
        )
        val expectedTransaction2 = BudgetTransaction(
            date = LocalDate.of(2023, 3, 26),
            amount = BigDecimal("120.45"),
            description = "OTHER SHOP WROCLAW PL"
        )

        transactions shouldBe listOf(expectedTransaction1, expectedTransaction2)
    }

    @Test
    fun `should return empty list when no transactions`() {
        val html = "<div></div>"

        val transactions = CitiHtmlTransactionParser(html).getTransactions()

        assertEquals(emptyList<BudgetTransaction>(), transactions)
    }

    @Test
    fun `should handle corrupted input`() {
        val html = """
            <div class="cbol-account-grid-body custom-data-grid-row-identifier   col-lg-12 col-md-12 col-sm-12 col-xs-12" data-index="1">
                ...
                <span id="wrong_id">25 mar 2023</span>
                ...
                <div class="wrong_desc">GENERIC SHOP 1    WROCLAW      PL</div>
                ...
                <span class="wrong_amount">-35,50</span>
                ...
            </div>
        """.trimIndent()

        val transactions = CitiHtmlTransactionParser(html).getTransactions()

        assertEquals(emptyList<BudgetTransaction>(), transactions)
    }
}