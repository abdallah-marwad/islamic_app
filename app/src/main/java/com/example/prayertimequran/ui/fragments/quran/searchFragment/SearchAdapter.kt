package com.example.prayertimequran.ui.fragments.quran.searchFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertimequran.R
import com.example.prayertimequran.data.models.quran.Aya
import com.example.prayertimequran.databinding.CustomSearchQuranBinding
import com.example.prayertimequran.databinding.CustomSoraaRvBinding
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters.ParentAdapter

class SearchAdapter :  RecyclerView.Adapter<SearchAdapter.ViewHolder>(){
    var list:ArrayList<Aya>? = ArrayList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding= CustomSearchQuranBinding.bind(view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_search_quran
                , parent, false)

        )
    }

    override fun getItemCount(): Int {
        return list?.size ?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listSearch = list!![position]

        holder.binding.customSearchAya.text = listSearch.aya_text
        holder.binding.customSearchSora.text =
            "سوره "+listSearch.sora_name_ar +"   آيه :"+listSearch.aya_no.toString()+
         "   صفحه : "+listSearch.page.toString()
        holder.binding.root.setOnClickListener {
            adapterOnClick(listSearch.page-1)
        }
    }

    lateinit var adapterOnClick : ((pageNumber :Int)->Unit)
}