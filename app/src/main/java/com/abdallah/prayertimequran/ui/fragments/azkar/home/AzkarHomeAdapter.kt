package com.abdallah.prayertimequran.ui.fragments.azkar.home

import android.view.View
import com.abdallah.prayertimequran.data.models.azkar.AzkarType
import com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters.ParentAdapter

class AzkarHomeAdapter() : ParentAdapter<AzkarType,String>(){

    private fun pagesNumber() : ArrayList<Int>{
        val list = ArrayList<Int>()
        for(i in 1..super.list.size) {
            list.add(i)
        }
        return list
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pageNumber = pagesNumber()[position]
        val zekrtype = list[position]
        holder.binding.customSoraName.text = zekrtype.zekrName
        holder.binding.customSoraNumber.text= pageNumber.toString()
        holder.binding.customSoraPages.visibility = View.GONE
        holder.binding.root.setOnClickListener {
            adapterOnClick?.let { it1 -> it1(zekrtype.zekrName) }
        }

    }

}