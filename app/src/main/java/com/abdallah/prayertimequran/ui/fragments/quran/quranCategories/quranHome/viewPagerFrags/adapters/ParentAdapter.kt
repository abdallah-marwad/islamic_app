package com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdallah.prayertimequran.databinding.CustomSoraaRvBinding


open class ParentAdapter<T, B> : RecyclerView.Adapter<ParentAdapter<T, B>.ViewHolder>() {
    open var list: ArrayList<T> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()

        }

    var showShimmer = true


    open inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: CustomSoraaRvBinding = CustomSoraaRvBinding.bind(view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(com.abdallah.prayertimequran.R.layout.custom_soraa_rv, parent, false)
        )
//
        return view
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    var adapterOnClick: ((pageNumber: B) -> Unit)? = null
}