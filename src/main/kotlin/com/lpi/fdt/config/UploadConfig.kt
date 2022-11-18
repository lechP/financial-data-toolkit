package com.lpi.fdt.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import java.io.FileInputStream

class UploadConfig {

    private val properties: UploadProperties by lazy {
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule.Builder().build())
        try {
            FileInputStream("dataupload.yaml").use {
                mapper.readValue(it, UploadProperties::class.java)
            }
        } catch (exception: MissingKotlinParameterException) {
            // TODO logger
            println("Could not read YAML file!")
            println(exception.message)
            throw RuntimeException(exception)
        }
    }

    val currencies: List<CommodityUploadConfig> by lazy {
        properties.currencies
    }

    val stocks: List<CommodityUploadConfig> by lazy {
        properties.stocks
    }
}

data class UploadProperties(
    val currencies: List<CommodityUploadConfig>,
    val stocks: List<CommodityUploadConfig>
)

data class CommodityUploadConfig(
    val symbol: String,
    val coordinates: UploadCoordinates
)

data class UploadCoordinates(
    val spreadsheetId: String,
    val range: String,
)