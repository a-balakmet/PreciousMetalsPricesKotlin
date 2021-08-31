package com.metals.precious.app.models

class PricesData : ArrayList<PricesDataItem>()

data class PricesDataItem(
    val d: String,
    val v: List<Double?>
)