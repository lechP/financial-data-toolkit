package com.lpi.fdt.experimental.htmlparser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class MHtmlTransactionParserTest {
    private val parser = MHtmlTransactionParser()

    @Test
    fun `should parse single charge transaction`() {
        val html = """
            <table>
                <tbody>
                    <tr>
                        <td></td>
                        <td>2023-03-25</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td>GENERIC SHOP 1 WROCLAW PL</td>
                        <td>-87.80</td>
                        <td></td>
                    </tr>
                </tbody>
            </table>
        """.trimIndent()

        val transactions = parser.parseTransactions(html)

        val expectedTransaction = BudgetTransaction(
            date = LocalDate.of(2023, 3, 25),
            amount = BigDecimal("87.80"),
            description = "GENERIC SHOP 1 WROCLAW PL"
        )
        assertEquals(listOf(expectedTransaction), transactions)
    }

    @Test
    fun `should parse single credit transaction`() {
        val html = """
            <table>
                <tbody>
                    <tr>
                        <td></td>
                        <td>2023-03-25</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td>return to GENERIC SHOP 1 WROCLAW PL</td>
                        <td></td>
                        <td>87.80</td>
                        <td></td>
                    </tr>
                </tbody>
            </table>
        """.trimIndent()

        val transactions = parser.parseTransactions(html)

        val expectedTransaction = BudgetTransaction(
            date = LocalDate.of(2023, 3, 25),
            amount = BigDecimal("-87.80"),
            description = "return to GENERIC SHOP 1 WROCLAW PL"
        )
        assertEquals(listOf(expectedTransaction), transactions)
    }

    @Test
    fun `should parse multiple transactions`() {
        val html = """
            <table>
                <tbody>
                    <tr>
                        <td></td>
                        <td>2023-03-25</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td>GENERIC SHOP 1 WROCLAW PL</td>
                        <td>-87.80</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>2023-03-26</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td>OTHER SHOP WROCLAW PL</td>
                        <td>-120.45</td>
                        <td></td>
                    </tr>
                </tbody>
            </table>
        """.trimIndent()

        val transactions = parser.parseTransactions(html)

        val expectedTransaction1 = BudgetTransaction(
            date = LocalDate.of(2023, 3, 25),
            amount = BigDecimal("87.80"),
            description = "GENERIC SHOP 1 WROCLAW PL"
        )
        val expectedTransaction2 = BudgetTransaction(
            date = LocalDate.of(2023, 3, 26),
            amount = BigDecimal("120.45"),
            description = "OTHER SHOP WROCLAW PL"
        )
        assertEquals(listOf(expectedTransaction1, expectedTransaction2), transactions)
    }

    @Test
    fun `should return empty list when no transactions`() {
        val html = "<table><tbody></tbody></table>"

        val transactions = parser.parseTransactions(html)

        assertEquals(emptyList<BudgetTransaction>(), transactions)
    }
}
