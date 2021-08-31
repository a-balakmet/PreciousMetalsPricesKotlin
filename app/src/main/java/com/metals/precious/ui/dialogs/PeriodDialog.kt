package com.metals.precious.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.metals.precious.R
import com.metals.precious.app.viewModels.MainViewModel
import com.metals.precious.databinding.DialogPeriodBinding

class PeriodDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogPeriodBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_period, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainViewModel : MainViewModel by viewModels(ownerProducer = { requireActivity() })
        binding.mainViewModel = mainViewModel
        binding.yearPicker.minValue = when (mainViewModel.metalName.value!!) {
            R.string.gold, R.string.silver -> 1968
            else -> 1990
        }
        binding.yearPicker.maxValue = mainViewModel.currentYear
        mainViewModel.period.value?.let {
            binding.yearPicker.value = it[1].toInt()
        }
        binding.yearPicker.wrapSelectorWheel = false

        binding.monthPicker.minValue = 1
        binding.monthPicker.maxValue = 12
        mainViewModel.period.value?.let {
            binding.monthPicker.value = it[0].toInt()
        }
        binding.monthPicker.wrapSelectorWheel = false
        binding.monthPicker.displayedValues = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

        binding.okBtn.setOnClickListener{
            dismiss()
            val aMonth = if (binding.monthPicker.value < 10) "0${binding.monthPicker.value}" else "${binding.monthPicker.value}"
            mainViewModel.initPeriod(monthAndYear = "$aMonth.${binding.yearPicker.value}")
            mainViewModel.setOperation(1)
        }
    }
}