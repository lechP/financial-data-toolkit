package com.lpi.fdt.util

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class UtilsTest {

    @Test
    fun `should split month into two ranges`() {

        val startDate = LocalDate.of(2022, 10, 1)
        val endDate = LocalDate.of(2022, 10, 31)
        val chunkSize = 20

        val result = chunkedDateRange(startDate, endDate, chunkSize)

        result shouldBe listOf(
            LocalDate.of(2022, 10, 1) to LocalDate.of(2022, 10, 20),
            LocalDate.of(2022, 10, 21) to LocalDate.of(2022, 10, 31),
        )
    }

    @Test
    fun `should handle start greater than end`() {

        val startDate = LocalDate.of(2022, 10, 2)
        val endDate = LocalDate.of(2022, 10, 1)
        val chunkSize = 1

        val result = chunkedDateRange(startDate, endDate, chunkSize)

        result shouldBe listOf(
            startDate to endDate
        )
    }

    @Test
    fun `should handle chunk bigger than range`() {

        val startDate = LocalDate.of(2022, 10, 1)
        val endDate = LocalDate.of(2022, 10, 5)
        val chunkSize = 10

        val result = chunkedDateRange(startDate, endDate, chunkSize)

        result shouldBe listOf(
            startDate to endDate
        )
    }

    @Test
    fun `should split into 1 day chunks`() {

        val startDate = LocalDate.of(2022, 10, 1)
        val endDate = LocalDate.of(2022, 10, 10)
        val chunkSize = 1

        val result = chunkedDateRange(startDate, endDate, chunkSize)

        result shouldHaveSize 10
    }

    @Test
    fun `should split into years`() {

        val startDate = LocalDate.of(2020, 3, 15)
        val endDate = LocalDate.of(2022, 4, 30)
        val chunkSize = 365

        val result = chunkedDateRange(startDate, endDate, chunkSize)

        result shouldBe listOf(
            LocalDate.of(2020, 3, 15) to LocalDate.of(2021, 3, 14),
            LocalDate.of(2021, 3, 15) to LocalDate.of(2022, 3, 14),
            LocalDate.of(2022, 3, 15) to LocalDate.of(2022, 4, 30),
        )
    }

}