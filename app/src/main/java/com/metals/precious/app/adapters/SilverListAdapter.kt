package com.metals.precious.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.metals.precious.R
import com.metals.precious.ThisApp.Companion.instance
import com.metals.precious.app.models.SilverPricesList
import com.metals.precious.databinding.SilverItemBinding

class SilverListAdapter(private val theList: SilverPricesList, private val currencyName: String) : RecyclerView.Adapter<SilverListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SilverItemBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SilverItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(theList[position]){
                binding.metalData = this
                binding.currencyName = currencyName
                if (position % 2 == 0) {
                    binding.thisLayout.setBackgroundColor(ContextCompat.getColor(instance, R.color.colorPrimaryDarkHalf))
                } else {
                    binding.thisLayout.setBackgroundColor(ContextCompat.getColor(instance, R.color.colorBackground))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return theList.size
    }
}