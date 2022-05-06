package com.lpi.fdt.routes

import com.lpi.fdt.currencies.NBPClient
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.time.LocalDate

fun Route.currencyRateRouting() {
    route("/currency-rates") {
        get {
            val symbol = call.request.queryParameters["symbol"] ?: "USD"
            val rawDateFrom = call.request.queryParameters["dateFrom"]
            val dateFrom = LocalDate.parse(rawDateFrom)
            val rawDateTo = call.request.queryParameters["dateTo"]
            val dateTo = LocalDate.parse(rawDateTo)

            val currencyRates = NBPClient().getCurrencyExchangeRates(symbol, dateFrom, dateTo)

            val response = CurrencyRatesResponse(
                currencyCode = currencyRates.code,
                exchangeRates = currencyRates.rates.map {
                    CurrencyRate(
                        effectiveDate = it.effectiveDate,
                        exchangeRate = it.mid
                    )
                })

            call.respond(response)
        }
    }
}

@Serializable
data class CurrencyRatesResponse(
    val currencyCode: String,
    val exchangeRates: List<CurrencyRate>
)

@Serializable
data class CurrencyRate(
    @Serializable(with = LocalDateSerializer::class)
    val effectiveDate: LocalDate,
    @Serializable(with = BigDecimalSerializer::class)
    val exchangeRate: BigDecimal
)

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }
}

object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toString())
    }
}