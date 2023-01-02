package com.lpi.fdt.util

import java.time.LocalDate

fun chunkedDateRange(startDate: LocalDate, endDate: LocalDate, chunkSizeInDays: Int): List<Pair<LocalDate, LocalDate>> =
    if (startDate > endDate) {
        listOf(startDate to endDate)
    } else {
        val milestones =
            generateSequence(startDate) { it.plusDays(chunkSizeInDays.toLong()) }.takeWhile { it <= endDate } + endDate
        milestones.zipWithNext().toList().let {
            it.dropLast(1).map { pair -> pair.first to pair.second.minusDays(1L) } + it.last()
        }
    }