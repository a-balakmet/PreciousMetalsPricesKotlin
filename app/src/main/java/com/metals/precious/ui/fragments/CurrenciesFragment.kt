package com.metals.precious.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.metals.precious.app.adapters.MetalsListAdapter
import com.metals.precious.app.models.MetalsPricesList
import com.metals.precious.databinding.FragmentCurrenciesBinding

class CurrenciesFragment : Fragment() {

    private lateinit var binding: FragmentCurrenciesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrenciesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            val pricesAdapter = bundle.getString("item")?.let { currency ->
                bundle.getParcelable<MetalsPricesList>("theList")?.let { list ->
                    MetalsListAdapter(
                        theList = list,
                        currencyName = currency,
                        isSilver = bundle.getBoolean("isSilver")
                    )
                }
            }
            binding.dataList.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = pricesAdapter
            }
        }
    }
}
