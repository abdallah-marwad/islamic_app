package com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters

import com.abdallah.prayertimequran.data.models.quran.Soraa

open class SoraaAdapter() : ParentAdapter<Soraa, Int>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val soraa= list[position]
        holder.binding.customSoraNumber.text= soraa.soraNumber.toString()
        holder.binding.customSoraName.text= soraa?.arabicName
        holder.binding.customSoraPages.text= "عدد الصفحات : "+(soraa.endPage!! -soraa?.startPage!! +1 ).toString()
        holder.binding.root.setOnClickListener {view->
            adapterOnClick?.let {
                it(soraa.startPage!!)
            }
        }
    }
}