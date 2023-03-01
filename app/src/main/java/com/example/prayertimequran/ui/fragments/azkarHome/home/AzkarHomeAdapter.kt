package com.example.prayertimequran.ui.fragments.azkarHome.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertimequran.R
import com.example.prayertimequran.data.models.azkar.AzkarType
import com.example.prayertimequran.databinding.CustomAzkarTypeBinding

class AzkarHomeAdapter() : RecyclerView.Adapter<AzkarHomeAdapter.ZekrTypeViewHolder>() {
    var azkarTypes:ArrayList<AzkarType> = ArrayList()

    set(azkarType){
        field = azkarType
        notifyDataSetChanged()
    }


    inner class ZekrTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: CustomAzkarTypeBinding
        init {
            binding = CustomAzkarTypeBinding.bind(view)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZekrTypeViewHolder {
        return ZekrTypeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_azkar_type, parent, false)
        )
    }
    override fun getItemCount(): Int {
        return azkarTypes.size
    }
    override fun onBindViewHolder(holder: ZekrTypeViewHolder, position: Int) {
        val zekrtype = azkarTypes[position]
        holder.binding.customAzkarTypeZekrName.text = zekrtype.zekrName

        holder.binding.root.setOnClickListener {
            adapterOnClick?.let { it1 -> it1(azkarTypes[position].zekrName) }
        }
    }

    var adapterOnClick : ((String)->Unit)? = null



}