package com.metals.precious.ui.uiAdds

import com.metals.precious.R
import com.metals.precious.ThisApp.Companion.instance
import com.metals.precious.ThisApp.Companion.numberFormat
import com.metals.precious.app.models.MetalsPrices
import com.metals.precious.app.models.SilverPrices

object BindingUtils {

    fun returnMetalName(resourceId: Int): String {
        return instance.getString(resourceId)
    }

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

    fun returnSilverCurrencyValue(data: SilverPrices, currencyName: String): String {
        return when (currencyName) {
            instance.getString(R.string.usd) -> if (data.USD != null) numberFormat.format(data.USD) else "-"
            instance.getString(R.string.gbp) -> if (data.USD != null) numberFormat.format(data.GBP) else "-"
            instance.getString(R.string.eur) -> if (data.USD != null) numberFormat.format(data.EUR) else "-"
            else -> "-"
        }
    }
}