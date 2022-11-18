package com.lpi.fdt.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import java.io.FileInputStream

class UploadConfig {

    val properties: UploadProperties by lazy {
        FileInputStream("dataupload.yaml")
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

// TODO
//  1. remove main
//  2. prepare separate gtters for currencies and stocks in UploadConfig
//  3. use new config in Main
//  4. remove those props from old Config and maybe rename it?
fun main() {
    println(UploadConfig().properties)
}