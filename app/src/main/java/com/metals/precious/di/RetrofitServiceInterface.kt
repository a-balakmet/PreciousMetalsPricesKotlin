package com.metals.precious.di

import com.metals.precious.app.models.PricesData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitServiceInterface {

    @GET("{suffix}")
    fun getDataFromApi(
        @Path("suffix") suffix: String
    ) : Call<PricesData>
}