package com.lpi.fdt.experimental.htmlparse.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class MilleAccountHtmlTransactionParserTest {

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

        val transactions = MilleAccountHtmlTransactionParser(html).getTransactions()

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

        val transactions = MilleAccountHtmlTransactionParser(html).getTransactions()

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

        val transactions = MilleAccountHtmlTransactionParser(html).getTransactions()

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

        val transactions = MilleAccountHtmlTransactionParser(html).getTransactions()

        assertEquals(emptyList<BudgetTransaction>(), transactions)
    }

    @Test
    fun `should derive date from description`() {
        val html = """
            <table>
                <tbody>
                    <tr>
                      <td align="right">PL79 1234</td>
                      <td align="right">2024-04-27</td>
                      <td align="right">2024-04-27</td>
                      <td align="right">[category]</td>
                      <td align="right"></td>
                      <td align="left"></td>
                      <td align="left">some shop in some place
            place continuation
            2024-04-25<br></td>
                      <td align="right">-59.00</td>
                      <td align="right">&nbsp;</td>
                      <td align="right">12.09</td>
                      <td align="right">PLN</td>
                    </tr>                
                </tbody>
            </table>
        """.trimIndent()

        val transactions = MilleAccountHtmlTransactionParser(html).getTransactions()

        val expectedTransaction = BudgetTransaction(
            date = LocalDate.of(2024, 4, 25),
            amount = BigDecimal("59.00"),
            description = "some shop in some place place continuation 2024-04-25"
        )
        assertEquals(listOf(expectedTransaction), transactions)
    }


    @Test
    fun `should use date from date column when not present in description`() {
        val html = """
            <table>
                <tbody>
                    <tr>
                      <td align="right">PL79 1234</td>
                      <td align="right">2024-04-25</td>
                      <td align="right">2024-04-25</td>
                      <td align="right">[category]</td>
                      <td align="right">1234 1234</td>
                      <td align="left">receiver</td>
                      <td align="left">some description<br></td>
                      <td align="right">-180.00</td>
                      <td align="right">&nbsp;</td>
                      <td align="right">11.83</td>
                      <td align="right">PLN</td>
                    </tr>                    
                </tbody>
            </table>
        """.trimIndent()

        val transactions = MilleAccountHtmlTransactionParser(html).getTransactions()

        val expectedTransaction = BudgetTransaction(
            date = LocalDate.of(2024, 4, 25),
            amount = BigDecimal("180.00"),
            description = "some description"
        )
        assertEquals(listOf(expectedTransaction), transactions)
    }
}
