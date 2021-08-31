package com.metals.precious.di

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RetrofitModule {

    private val baseURL = "https://prices.lbma.org.uk/json/"

    @Singleton
    @Provides
    fun getRetrofitServiceInterface(retrofit: Retrofit) : RetrofitServiceInterface {
        return retrofit.create(RetrofitServiceInterface::class.java)
    }

    @Singleton
    @Provides
    fun getRetrofitInstance() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}