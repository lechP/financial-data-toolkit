package com.lpi.fdt.experimental.htmlparse

const val PKO = "PKO"
const val MILLE_A = "MILLE"
const val MILLE_CC = "MILLE_CC"
const val CITI = "CITI"

private val pkoFingerprint = listOf("PKOInteligo", "<h3>Operacje zrealizowane</h3>", "<tr><td>Numer karty</td>")
private val milleCommon = listOf("<table border=\"1\">", "<td align=\"right\">Numer rachunku/karty</td>", "</table><br></body>")
private val milleAccountFingerprint = milleCommon + "PL79"
private val milleCCFingerprint = milleCommon + "XXXX XXXX"
private val citiFingerprint = listOf("tbd")

val fingerprintsToKeys = mapOf(
    pkoFingerprint to PKO,
    milleAccountFingerprint to MILLE_A,
    milleCCFingerprint to MILLE_CC,
    citiFingerprint to CITI
)