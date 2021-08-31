package com.metals.precious.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.metals.precious.R
import com.metals.precious.app.adapters.MetalsListAdapter
import com.metals.precious.app.adapters.SilverListAdapter
import com.metals.precious.app.viewModels.MainViewModel
import com.metals.precious.databinding.FragmentCurrenciesBinding

class CurrenciesFragment : Fragment() {

    private lateinit var binding: FragmentCurrenciesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCurrenciesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainViewModel : MainViewModel by viewModels(ownerProducer = { requireActivity() })
        arguments?.let { bundle ->
            mainViewModel.metalName.value?.let { metal->
                when (metal) {
                    R.string.silver -> {
                        mainViewModel.silverPrices.value?.let { prices->
                            val pricesAdapter = bundle.getString("item")?.let { currency->
                                SilverListAdapter(
                                    theList = prices,
                                    currencyName = currency
                                )
                            }
                            binding.dataList.apply {
                                layoutManager = LinearLayoutManager(activity)
                                adapter = pricesAdapter
                            }
                        }
                    }
                    else -> {
                        mainViewModel.metalsPrices.value?.let { prices->
                            val pricesAdapter = bundle.getString("item")?.let { currency->
                                MetalsListAdapter(
                                    theList = prices,
                                    currencyName = currency
                                )
                            }
                            binding.dataList.apply {
                                layoutManager = LinearLayoutManager(activity)
                                adapter = pricesAdapter
                            }
                        }
                    }
                }
            }
        }
    }
}