package com.lpi.fdt.experimental.htmlparse

import com.lpi.fdt.experimental.htmlparse.parser.MilleHtmlTransactionParser
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ParserFactoryTest {

    @Test
    fun `should create MilleHtmlTransactionParser`() {
        val fingerprint = fingerprintsToKeys.filter { it.value == MILLE }.keys.first()
        val content = fingerprint.joinToString(separator = " lorem ipsum ") { it }
        val parser = ParserFactory().createParser(content)
        (parser is MilleHtmlTransactionParser) shouldBe true
    }

    @Test
    fun `should throw exception when content is not matched with any parser`() {
        val content = "lorem ipsum"
        ParserFactory().createParser(content) shouldBe null
    }
}

