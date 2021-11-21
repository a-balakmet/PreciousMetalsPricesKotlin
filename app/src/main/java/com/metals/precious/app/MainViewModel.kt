package com.metals.precious.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.metals.precious.R
import com.metals.precious.ThisApp
import com.metals.precious.app.models.MetalPricesState
import com.metals.precious.app.models.MetalsPrices
import com.metals.precious.app.models.MetalsPricesList
import com.metals.precious.app.models.PricesData
import com.metals.precious.di.RetrofitServiceInterface
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var mService: RetrofitServiceInterface

    var operation: MutableLiveData<Int> = MutableLiveData()
    var metalName: Int = R.string.gold
    var period: ArrayList<String> = ArrayList()
    val currentYear = yearFormatter.format(Date()).toInt()

    var amData: PricesData? = null
    var pmData: PricesData? = null

    private val _metalPricesState = MutableStateFlow<MetalPricesState>(MetalPricesState.Loading)
    val metalPricesState: StateFlow<MetalPricesState> = _metalPricesState

    init {
        (application as ThisApp).getRetrofitComponent().inject(this)
    }

    fun setOperation(o: Int) {
        operation.value = o
    }

    fun initLoadingData(title: Int) {
        metalName = title
        loadMetalPrices()
    }

    fun initPeriod(monthAndYear: MutableList<String>) {
        period.clear()
        monthAndYear.map {
            period.add(it)
        }
        loadMetalPrices()
    }

    fun getCurrentData() {
        val calendar = GregorianCalendar()
        calendar.time = Date()
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            calendar.add(Calendar.MONTH, -1)
            initPeriod(monthAndYear = monthYearFormatter.format(calendar.time).split(".").toMutableList())
        } else {
            initPeriod(monthAndYear = monthYearFormatter.format(Date()).split(".").toMutableList())
        }
    }

    private fun loadMetalPrices() = viewModelScope.launch {
        _metalPricesState.value = MetalPricesState.Loading
        var dataScope: ArrayList<String> = arrayListOf()
        when (metalName) {
            R.string.gold -> dataScope = arrayListOf(goldAm, goldPm)
            R.string.silver -> dataScope = arrayListOf(argentum)
            R.string.platinum -> dataScope = arrayListOf(platAm, platPm)
            R.string.palladium -> dataScope = arrayListOf(pallAm, pallPm)
        }
        when (dataScope.size) {
            1 -> makeApiCall(dataScope[0])
            2 -> {
                launch {
                    val execution = async(IO) {
                        makeApiCall(dataScope[0])
                        delay(1309)
                        makeApiCall(dataScope[1])
                    }
                    execution.await()
                }
            }
        }
    }

    private fun createListOfPrices() {
        val prices = MetalsPricesList()
        val searchedDate = "${period[1]}-${period[0]}"
        amData?.let { morningPrices ->
            for (amPrice in morningPrices) {
                if (amPrice.d.contains(searchedDate)) {
                    if (pmData != null) {
                        pmData?.let { afternoonPrices ->
                            for (pmPrice in afternoonPrices) {
                                if (pmPrice.d == amPrice.d) {
                                    lbmaDateFormat.parse(amPrice.d)?.let { lbmaDate ->
                                        val metalPrice = MetalsPrices(
                                            date = appDateFormat.format(lbmaDate),
                                            amUSD = amPrice.v[0],
                                            amGBP = amPrice.v[1],
                                            amEUR = amPrice.v[2],
                                            pmUSD = pmPrice.v[0],
                                            pmGBP = pmPrice.v[1],
                                            pmEUR = pmPrice.v[2]
                                        )
                                        prices.add(metalPrice)
                                    }
                                }
                            }
                        }
                    } else {
                        if (amPrice.d.contains(searchedDate)) {
                            lbmaDateFormat.parse(amPrice.d)?.let { lbmaDate ->
                                val metalPrice = MetalsPrices(
                                    date = appDateFormat.format(lbmaDate),
                                    amUSD = amPrice.v[0],
                                    amGBP = amPrice.v[1],
                                    amEUR = amPrice.v[2],
                                    pmUSD = null,
                                    pmGBP = null,
                                    pmEUR = null
                                )
                                prices.add(metalPrice)
                            }
                        }
                    }
                }
            }
        }
        _metalPricesState.value = MetalPricesState.PricesList(prices)
    }

    private fun makeApiCall(suffix: String) {
        mService.getDataFromApi(suffix = suffix).enqueue(object : Callback<PricesData> {
            override fun onResponse(call: Call<PricesData>, response: Response<PricesData>) {
                if (response.isSuccessful) {
                    when {
                        suffix.contains("am") -> amData = response.body()
                        suffix.contains("pm") -> {
                            pmData = response.body()
                            createListOfPrices()
                        }
                        suffix == argentum -> {
                            amData = response.body()
                            createListOfPrices()
                        }
                    }
                } else {
                    _metalPricesState.value = MetalPricesState.Error("error")
                }
            }

            override fun onFailure(call: Call<PricesData>, t: Throwable) {
                when {
                    suffix.contains("am") -> amData = null
                    suffix.contains("pm") -> pmData = null
                }
                _metalPricesState.value = MetalPricesState.Error("no net")
            }
        })
    }

    companion object {
        private val monthYearFormatter by lazy { SimpleDateFormat("MM.yyyy", Locale.ENGLISH) }
        private val yearFormatter by lazy { SimpleDateFormat("yyyy", Locale.ENGLISH) }
        private val lbmaDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH) }
        private val appDateFormat by lazy { SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH) }
        private const val goldAm = "gold_am.json"
        private const val goldPm = "gold_pm.json"
        private const val argentum = "silver.json"
        private const val platAm = "platinum_am.json"
        private const val platPm = "platinum_pm.json"
        private const val pallAm = "palladium_am.json"
        private const val pallPm = "palladium_pm.json"
    }
}