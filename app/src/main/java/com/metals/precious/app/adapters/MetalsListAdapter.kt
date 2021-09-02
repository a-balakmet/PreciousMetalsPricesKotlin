package com.metals.precious.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.metals.precious.R
import com.metals.precious.app.models.MetalsPricesList
import com.metals.precious.databinding.MetalsItemBinding

class MetalsListAdapter(private val theList: MetalsPricesList, private val currencyName: String) : RecyclerView.Adapter<MetalsListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MetalsItemBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MetalsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(theList[position]){
                binding.metalData = this
                binding.currencyName = currencyName
                if (position % 2 == 0) {
                    binding.thisLayout.setBackgroundResource(R.color.colorPrimaryDarkHalf)
                } else {
                    binding.thisLayout.setBackgroundResource(R.color.colorBackground)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return theList.size
    }
}
