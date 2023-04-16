package com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters

import android.view.View
import com.example.prayertimequran.data.models.quran.Juzz

class JuzzAdapter : ParentAdapter<Juzz,Int>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val juzz= list[position]
        holder.binding.customSoraNumber.text= juzz.jozzNumber.toString()
        holder.binding.customSoraName.text= "جزء "+juzz.jozzNumber.toString()
        holder.binding.customSoraPages.text= "من : "+ juzz.startPage.toString() +" الى :"+juzz.endPage.toString()
        holder.binding.root.setOnClickListener {
            adapterOnClick?.let {
                it(juzz.startPage!!)
            }
        }
    }

}