package com.lpi.fdt.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.LoggerFactory
import java.io.FileInputStream

class UploadConfig {

    private val properties: UploadProperties by lazy {
        loadProperties("dataupload.yaml")
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

inline fun <reified T> loadProperties(filename: String): T {
    val mapper = ObjectMapper(YAMLFactory())
    mapper.registerModule(KotlinModule.Builder().build())
    try {
        return FileInputStream(filename).use {
            mapper.readValue(it, T::class.java)
        }
    } catch (exception: MismatchedInputException) {
        val logger = LoggerFactory.getLogger("Properties loader")
        logger.error("Could not read YAML file!", exception)
        throw RuntimeException(exception)
    }
}