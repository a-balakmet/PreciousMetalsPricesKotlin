package com.metals.precious

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.metals.precious.di.DaggerRetrofitComponent
import com.metals.precious.di.RetrofitComponent
import com.metals.precious.di.RetrofitModule
import java.text.NumberFormat
import java.util.*

class ThisApp : Application () {

    companion object{
        lateinit var instance: Application
        lateinit var numberFormat: NumberFormat
        var isOnline: Boolean = false
    }

    private lateinit var retrofitComponent : RetrofitComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        numberFormat.minimumFractionDigits = 3
        isOnline = isNetwork(this)
        retrofitComponent = DaggerRetrofitComponent.builder()
            .retrofitModule(RetrofitModule())
            .build()
    }

    fun getRetrofitComponent() : RetrofitComponent {
        return retrofitComponent
    }

    @Suppress("DEPRECATION")
    fun isNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}