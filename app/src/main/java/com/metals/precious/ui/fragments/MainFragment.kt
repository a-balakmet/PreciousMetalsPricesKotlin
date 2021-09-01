package com.metals.precious.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.metals.precious.R
import com.metals.precious.app.adapters.FragmentAdapter
import com.metals.precious.databinding.FragmentMainBinding
import com.metals.precious.app.viewModels.MainViewModel
import kotlin.system.exitProcess
import android.provider.Settings
import com.metals.precious.ThisApp
import com.metals.precious.ThisApp.Companion.isOnline

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val mainViewModel : MainViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        if (!isOnline) {
            mainViewModel.setOperation(2)
        }
        mainViewModel.operation.observe(viewLifecycleOwner, {
            it?.let {
                when (it) {
                    0 -> {
                        findNavController().navigate(R.id.action_mainFragment_to_periodDialog)
                    }
                    1 -> {
                        observeMetal()
                    }
                    2 -> {
                        findNavController().navigate(R.id.action_mainFragment_to_noNetDialog)
                    }
                    3 -> {
                        val intent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(intent)
                        exitProcess(0)
                    }
                }
            }
        })
    }

    private fun observeMetal(){
        mainViewModel.metalName.observe(viewLifecycleOwner, {
            binding.showProgress = true
            it?.let {
                if (!ThisApp().isNetwork(requireActivity())) {
                    mainViewModel.setOperation(2)
                } else {
                    mainViewModel.getMetalsData()
                }
                when (it){
                    R.string.silver -> {
                        mainViewModel.silverPrices.observe(viewLifecycleOwner, {theList->
                            theList?.let {
                                if (theList.size>0) {
                                    activity?.let { fragmentActivity ->
                                        binding.pager.adapter = FragmentAdapter(
                                            manager = fragmentActivity.supportFragmentManager,
                                            lifecycle = lifecycle,
                                            currencyTabs = resources.getStringArray(R.array.currencies)
                                        )
                                        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                                            tab.text = resources.getStringArray(R.array.currencies)[position]
                                        }.attach()
                                        binding.showProgress = false
                                    }
                                } else {
                                    mainViewModel.getCurrentData()
                                }
                            }
                        })
                    }
                    else -> {
                        mainViewModel.metalsPrices.observe(viewLifecycleOwner, {theList->
                            theList?.let {
                                if (theList.size>0) {
                                    activity?.let { fragmentActivity ->
                                        binding.pager.adapter = FragmentAdapter(
                                            manager = fragmentActivity.supportFragmentManager,
                                            lifecycle = lifecycle,
                                            currencyTabs = resources.getStringArray(R.array.currencies)
                                        )
                                        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                                            tab.text = resources.getStringArray(R.array.currencies)[position]
                                        }.attach()
                                        binding.showProgress = false
                                    }
                                } else {
                                    mainViewModel.getCurrentData()
                                }
                            }
                        })
                    }
                }
                mainViewModel.silverPrices.value = null
                mainViewModel.metalsPrices.value = null
            }
        })
    }
}
