package com.metals.precious.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.metals.precious.R
import com.metals.precious.ThisApp.Companion.instance
import com.metals.precious.ThisApp.Companion.isOnline
import com.metals.precious.app.MainViewModel
import com.metals.precious.app.adapters.FragmentAdapter
import com.metals.precious.app.models.MetalPricesState
import com.metals.precious.app.utils.gone
import com.metals.precious.app.utils.visible
import com.metals.precious.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.collect
import kotlin.system.exitProcess

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val mainViewModel : MainViewModel by activityViewModels()
    //private val mainViewModel : MainViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val buttonsArray = arrayOf(binding.au, binding.ag, binding.pl, binding.pt)
        mainViewModel.getCurrentData()
        lifecycleScope.launchWhenStarted {
            mainViewModel.metalPricesState.collect{
                when (it) {
                    is MetalPricesState.PricesList -> {
                        if (it.theList.size>0) {
                            activity?.let { fragmentActivity ->
                                binding.pager.adapter = FragmentAdapter(
                                    manager = fragmentActivity.supportFragmentManager,
                                    lifecycle = lifecycle,
                                    theList = it.theList,
                                    currencyTabs = resources.getStringArray(R.array.currencies),
                                    isSilver = mainViewModel.metalName == R.string.silver
                                )
                                TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                                    tab.text = resources.getStringArray(R.array.currencies)[position]
                                }.attach()

                                binding.progressBar.gone()
                                binding.pager.visible()
                            }
                        } else {
                            mainViewModel.getCurrentData()
                        }
                    }
                    is MetalPricesState.Error -> {
                        binding.progressBar.gone()
                        binding.pager.gone()
                        if (it.errorMessage == "error") {
                            Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }
                    is MetalPricesState.Loading -> {
                        binding.progressBar.visible()
                        binding.pager.gone()
                        mainViewModel.period.let { period->
                            binding.month.text = period[0]
                            binding.year.text = period[1]
                        }
                        mainViewModel.metalName.let { metal->
                            binding.metal.text = instance.getString(metal)
                            buttonsArray.map { theButton ->
                                theButton.setTextColor(ContextCompat.getColor(instance, R.color.colorPrimaryDark))
                                when(metal) {
                                    R.string.gold -> binding.au.setTextColor(ContextCompat.getColor(instance, R.color.colorAccent))
                                    R.string.silver -> binding.ag.setTextColor(ContextCompat.getColor(instance, R.color.colorAccent))
                                    R.string.platinum -> binding.pl.setTextColor(ContextCompat.getColor(instance, R.color.colorAccent))
                                    R.string.palladium -> binding.pt.setTextColor(ContextCompat.getColor(instance, R.color.colorAccent))
                                }
                            }
                        }
                    }
                    is MetalPricesState.PeriodToShow -> {
                        mainViewModel.initPeriod(it.newPeriod)
                    }
                    //else -> Unit
                }
            }
        }

        if (!isOnline) {
            mainViewModel.setOperation(2)
        }
        mainViewModel.operation.observe(viewLifecycleOwner, {
            it?.let {
                when (it) {
                    0 -> findNavController().navigate(R.id.action_mainFragment_to_periodDialog)
                    //1 -> mainViewModel.loadMetalPrices()
                    2 -> findNavController().navigate(R.id.action_mainFragment_to_noNetDialog)
                    3 -> {
                        val intent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(intent)
                        exitProcess(0)
                    }
                }
            }
        })
    }
}
