package com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters

import android.view.View
import com.example.prayertimequran.data.models.quran.SavedPage

class SavedAdapter : ParentAdapter<SavedPage,Int>() {

    private fun pagesNumber() : ArrayList<Int>{
        val list = ArrayList<Int>()
        for(i in 1..super.list.size) {
            list.add(i)
        }
        return list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val page= list[position]
        val pageNumber = pagesNumber()[position]
        holder.binding.customSoraNumber.text= pageNumber.toString()
        holder.binding.customSoraName.text= "رقم الصفحه : " +page.pageNumber
        holder.binding.customSoraPages.visibility= View.GONE
        holder.binding.root.setOnClickListener {view->
            adapterOnClick?.let {
                it(page.pageNumber)
            }
        }
    }
}