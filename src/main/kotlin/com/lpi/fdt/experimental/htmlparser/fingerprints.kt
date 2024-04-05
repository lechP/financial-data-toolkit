package com.lpi.fdt.experimental.htmlparser

const val PKO = "PKO"
const val MILLE = "MILLE"
const val CITI = "CITI"

private val pkoFingerprint = listOf("PKOInteligo", "<h3>Operacje zrealizowane</h3>", "<tr><td>Numer karty</td>")
private val milleFingerprint = listOf("<table border=\"1\">", "<td align=\"right\">Numer rachunku/karty</td>", "</table><br></body>")
private val citiFingerprint = listOf("tbd")

val fingerprintsToKeys = mapOf(
    pkoFingerprint to PKO,
    milleFingerprint to MILLE,
    citiFingerprint to CITI
)