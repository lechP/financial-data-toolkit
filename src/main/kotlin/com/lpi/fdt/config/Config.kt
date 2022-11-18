package com.lpi.fdt.config

import java.io.FileInputStream
import java.util.*

class Config {

    private val properties: Properties by lazy {
        val props = Properties()
        props.load(FileInputStream("application.properties"))
        props
    }

    val googleCredentials: GoogleCredentials by lazy {
        GoogleCredentials(
            properties.getProperty("google.clientId"),
            properties.getProperty("google.clientSecret"),
        )
    }
}

data class GoogleCredentials(
    val clientId: String,
    val clientSecret: String
)