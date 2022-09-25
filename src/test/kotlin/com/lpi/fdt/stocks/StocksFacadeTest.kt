package com.lpi.fdt.stocks

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

internal class StocksFacadeTest {

    private val stocksClient = mockk<StocksClient>()
    private val sut = StocksFacade(stocksClient)

    @Test
    fun `should parse example data with 'volume' field`() = runBlocking {
        val input = "first,row,to,be,ignored\r\n" +
                "2022-08-01,10.00,11.00,9.95,10.20,4000"

        coEvery { stocksClient.getValueHistory("any") } returns input

        val result = sut.getHistoricalValues("any")

        result shouldHaveSize 1
        with(result[0]) {
            date shouldBe LocalDate.of(2022,8,1)
            open shouldBe BigDecimal("10.00")
            max shouldBe BigDecimal("11.00")
            min shouldBe BigDecimal("9.95")
            close shouldBe BigDecimal("10.20")
            volume shouldBe BigDecimal("4000")
        }
    }

    @Test
    fun `should parse example data without 'volume' field`() = runBlocking {
        val input = "first,row,to,be,ignored\r\n" +
                "2022-08-01,10.00,11.00,9.95,10.20"

        coEvery { stocksClient.getValueHistory("any") } returns input

        val result = sut.getHistoricalValues("any")

        result shouldHaveSize 1
        with(result[0]) {
            date shouldBe LocalDate.of(2022,8,1)
            open shouldBe BigDecimal("10.00")
            max shouldBe BigDecimal("11.00")
            min shouldBe BigDecimal("9.95")
            close shouldBe BigDecimal("10.20")
            volume shouldBe null
        }
    }

    @Test
    fun `should parse longer data`(): Unit = runBlocking {
        val input = "first,row,to,be,ignored\r\n" +
                "2022-08-01,10.00,11.00,9.95,10.20\r\n" +
                "2022-08-01,10.00,11.00,9.95,10.20\r\n" +
                "2022-08-01,10.00,11.00,9.95,10.20\r\n"

        coEvery { stocksClient.getValueHistory("any") } returns input

        val result = sut.getHistoricalValues("any")

        result shouldHaveSize 3
    }

    @Test
    fun `should ignore whitespaces at the end of file`(): Unit = runBlocking {
        val input = "first,row,to,be,ignored\r\n" +
                "2022-08-01,10.00,11.00,9.95,10.20\r\n" +
                "2022-08-01,10.00,11.00,9.95,10.20\r\n" +
                "   \r\n" +
                "         \r\n" +
                " "

        coEvery { stocksClient.getValueHistory("any") } returns input

        val result = sut.getHistoricalValues("any")

        result shouldHaveSize 2
    }

    @Test
    fun `should handle empty input`(): Unit = runBlocking {
        val input = ""

        coEvery { stocksClient.getValueHistory("any") } returns input

        val result = sut.getHistoricalValues("any")

        result.shouldBeEmpty()
    }

}