package com.metals.precious.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.DialogFragment
import com.metals.precious.R
import com.metals.precious.databinding.DialogNoNetBinding
import androidx.fragment.app.viewModels
import com.metals.precious.app.viewModels.MainViewModel

class NoNetDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: DialogNoNetBinding = inflate(inflater, R.layout.dialog_no_net, container, false)
        binding.okBtn.setOnClickListener{
            val mainViewModel : MainViewModel by viewModels(ownerProducer = { requireActivity() })
            mainViewModel.setOperation(3)
        }
        return binding.root
    }
}