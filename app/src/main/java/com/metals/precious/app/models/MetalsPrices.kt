package com.metals.precious.app.models

data class MetalsPrices(
    val date: String,
    val amUSD: Double?,
    val amGBP: Double?,
    val amEUR: Double?,
    val pmUSD: Double?,
    val pmGBP: Double?,
    val pmEUR: Double?
)
