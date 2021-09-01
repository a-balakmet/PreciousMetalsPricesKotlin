package com.metals.precious.app.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.metals.precious.R
import com.metals.precious.ThisApp
import com.metals.precious.app.models.*
import com.metals.precious.di.RetrofitServiceInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var mService : RetrofitServiceInterface

    var operation: MutableLiveData<Int> = MutableLiveData()
    var metalName: MutableLiveData<Int> = MutableLiveData()
    var period: MutableLiveData<MutableList<String>> = MutableLiveData()
    val currentYear = yearFormatter.format(Date()).toInt()

    var amData: PricesData? = null
    var pmData: PricesData? = null

    var metalsPrices: MutableLiveData<MetalsPricesList> = MutableLiveData()
    var silverPrices: MutableLiveData<SilverPricesList> = MutableLiveData()

    init {
        (application as ThisApp).getRetrofitComponent().inject(this)
        setTitle(R.string.gold)
        getCurrentData()
    }

    fun setOperation(o: Int) {
        operation.value = o
    }

    fun setTitle(title: Int) {
        metalName.value = title
    }

    fun initPeriod(monthAndYear: String) {
        period.value = monthAndYear.split(".").toMutableList()
    }

    fun getCurrentData() {
        val calendar = GregorianCalendar()
        calendar.time = Date()
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            //val cal = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)
            initPeriod(monthAndYear = monthYearFormatter.format(calendar.time))
        } else {
            initPeriod(monthAndYear = monthYearFormatter.format(Date()))
        }
        setOperation(1)
    }

    fun getMetalsData() {
        CoroutineScope(IO).launch {
            var dataScope: ArrayList<String> = arrayListOf()
            metalName.value?.let {
                when (it) {
                    R.string.gold -> dataScope = arrayListOf(goldAm, goldPm)
                    R.string.silver -> dataScope = arrayListOf(argentum)
                    R.string.platinum -> dataScope = arrayListOf(platAm, platPm)
                    R.string.palladium -> dataScope = arrayListOf(pallAm, pallPm)
                }
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
    }

    private fun getMetalsPricesOfPeriod() : MetalsPricesList {
        val prices = MetalsPricesList()
        val searchedDate = "${period.value!![1]}-${period.value!![0]}"
        amData?.let { morningPrices ->
            for (amPrice in morningPrices) {
                if (amPrice.d.contains(searchedDate)){
                    pmData?.let { afternoonPrices->
                        for (pmPrice in afternoonPrices) {
                            if (pmPrice.d == amPrice.d) {
                                lbmaDateFormat.parse(amPrice.d)?.let {lbmaDate->
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
                }
            }
        }
        return prices
    }

    private fun getSilverMetalsOfPeriod() : SilverPricesList {
        val prices = SilverPricesList()
        val searchedDate = "${period.value!![1]}-${period.value!![0]}"
        amData?.let { morningPrices ->
            for (amPrice in morningPrices) {
                if (amPrice.d.contains(searchedDate)){
                    lbmaDateFormat.parse(amPrice.d)?.let {lbmaDate->
                        val silverPrice  = SilverPrices(
                            date = appDateFormat.format(lbmaDate),
                            USD = amPrice.v[0],
                            GBP = amPrice.v[1],
                            EUR = amPrice.v[2],
                        )
                        prices.add(silverPrice)
                    }
                }
            }
        }
        return prices
    }

    private fun makeApiCall(suffix: String) {
        val call: Call<PricesData> = mService.getDataFromApi(suffix = suffix)
        call.enqueue(object : Callback<PricesData>{
            override fun onResponse(call: Call<PricesData>, response: Response<PricesData>) {
                if (response.isSuccessful) {
                    when {
                        suffix.contains("am") -> amData = response.body()
                        suffix.contains("pm") -> {
                            pmData = response.body()
                            metalsPrices.value = getMetalsPricesOfPeriod()
                        }
                        suffix == argentum -> {
                            amData = response.body()
                            silverPrices.value = getSilverMetalsOfPeriod()
                        }
                    }
                } else {
                    when {
                        suffix.contains("am") -> amData = null
                        suffix.contains("pm") -> pmData = null
                    }
                }
            }
            override fun onFailure(call: Call<PricesData>, t: Throwable) {
                when {
                    suffix.contains("am") -> amData = null
                    suffix.contains("pm") -> pmData = null
                }
            }
        })
    }

    companion object{
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