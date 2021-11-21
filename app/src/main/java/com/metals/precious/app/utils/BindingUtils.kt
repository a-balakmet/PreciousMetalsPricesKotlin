package com.metals.precious.app.utils

import com.metals.precious.R
import com.metals.precious.ThisApp.Companion.instance
import com.metals.precious.ThisApp.Companion.numberFormat
import com.metals.precious.app.models.MetalsPrices

object BindingUtils {

    fun returnMetalsCurrencyValue(data: MetalsPrices, currencyName: String, isAm: Boolean): String {
        var currencyValue = ""
        when(currencyName) {
            instance.getString(R.string.usd) -> {
                currencyValue = if (isAm) {
                    if (data.amUSD != null) numberFormat.format(data.amUSD) else "-"
                } else {
                    if (data.pmUSD != null) numberFormat.format(data.amUSD) else "-"
                }
            }
            instance.getString(R.string.gbp) -> {
                currencyValue = if (isAm) {
                    if (data.amGBP != null) numberFormat.format(data.amGBP) else "-"
                } else {
                    if (data.pmGBP != null) numberFormat.format(data.pmGBP) else "-"
                }
            }
            instance.getString(R.string.eur) -> {
                currencyValue = if (isAm) {
                    if (data.amEUR != null) numberFormat.format(data.amEUR) else "-"
                } else {
                    if (data.pmEUR != null) numberFormat.format(data.pmEUR) else "-"
                }
            }
        }
        return currencyValue
    }
}