package com.metals.precious.app.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MetalsPricesList : ArrayList<MetalsPrices>(), Parcelable

data class MetalsPrices(
    val date: String,
    val amUSD: Double?,
    val amGBP: Double?,
    val amEUR: Double?,
    val pmUSD: Double?,
    val pmGBP: Double?,
    val pmEUR: Double?
)