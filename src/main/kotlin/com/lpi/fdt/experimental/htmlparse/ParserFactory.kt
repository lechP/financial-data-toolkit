package com.lpi.fdt.experimental.htmlparse

import com.lpi.fdt.experimental.htmlparse.parser.CitiHtmlTransactionParser
import com.lpi.fdt.experimental.htmlparse.parser.HtmlTransactionParser
import com.lpi.fdt.experimental.htmlparse.parser.MilleHtmlTransactionParser
import com.lpi.fdt.experimental.htmlparse.parser.PKOCreditCardHtmlTransactionParser

class ParserFactory {
    fun createParser(content: String): HtmlTransactionParser {
        val parserKey = fingerprintsToKeys.entries.find { (fingerprint, _) -> fingerprint.all { content.contains(it) } }?.value
        return when (parserKey) {
            PKO -> PKOCreditCardHtmlTransactionParser(content)
            MILLE -> MilleHtmlTransactionParser(content)
            CITI -> CitiHtmlTransactionParser(content)
            else -> throw IllegalArgumentException("Could not match content with parser fingerprint: $content")
        }
    }

}