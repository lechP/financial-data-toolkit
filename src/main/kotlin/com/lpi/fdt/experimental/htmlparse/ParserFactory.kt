package com.lpi.fdt.experimental.htmlparse

import com.lpi.fdt.experimental.htmlparse.parser.*

class ParserFactory {
    fun createParser(content: String): HtmlTransactionParser? {
        val parserKey = fingerprintsToKeys.entries.find { (fingerprint, _) -> fingerprint.all { content.contains(it) } }?.value
        return when (parserKey) {
            PKO -> PKOCreditCardHtmlTransactionParser(content)
            MILLE_A -> MilleAccountHtmlTransactionParser(content)
            MILLE_CC -> MilleCreditCardHtmlTransactionParser(content)
            CITI -> CitiHtmlTransactionParser(content)
            else -> null
        }
    }

}