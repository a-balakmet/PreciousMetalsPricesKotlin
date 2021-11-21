package com.metals.precious.app.models

sealed class MetalPricesState {
    data class PricesList(val theList: MetalsPricesList) : MetalPricesState()
    data class Error(val errorMessage: String) : MetalPricesState()
    object Loading : MetalPricesState()
    data class PeriodToShow (val newPeriod: ArrayList<String>): MetalPricesState()
}
