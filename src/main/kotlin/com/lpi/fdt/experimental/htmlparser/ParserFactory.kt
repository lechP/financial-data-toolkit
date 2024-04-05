package com.lpi.fdt.experimental.htmlparser

class ParserFactory {
    fun createParser(content: String): HtmlTransactionParser {
        val parserKey = fingerprintsToKeys.entries.find { (fingerprint, _) -> fingerprint.all { content.contains(it) } }?.value
        return when (parserKey) {
            PKO -> PKOCreditCardHtmlTransactionParser()
            MILLE -> MHtmlTransactionParser()
            CITI -> CitiHtmlTransactionParser()
            else -> throw IllegalArgumentException("Could not match content with parser fingerprint: $content")
        }
    }

}