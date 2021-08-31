package com.metals.precious.app.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.metals.precious.ui.fragments.CurrenciesFragment

class FragmentAdapter(
    manager: FragmentManager,
    lifecycle: Lifecycle,
    private val currencyTabs: Array<String>
) : FragmentStateAdapter(manager, lifecycle) {

    override fun getItemCount(): Int {
        return currencyTabs.size
    }

    override fun createFragment(position: Int): Fragment {
        val tabItem = CurrenciesFragment()
        for (i in currencyTabs.indices) {
            when (position) {
                i -> {
                    val bundle = Bundle()
                    bundle.putString("item", currencyTabs[i])
                    tabItem.arguments = bundle
                }
            }
        }
        return tabItem
    }
}