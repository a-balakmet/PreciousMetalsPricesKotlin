package com.metals.precious.di

import com.metals.precious.app.viewModels.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class])
interface RetrofitComponent {

    fun inject(mainViewModel: MainViewModel)
}