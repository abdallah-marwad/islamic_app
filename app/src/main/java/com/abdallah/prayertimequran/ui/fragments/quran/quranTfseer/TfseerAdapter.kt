package com.abdallah.prayertimequran.ui.fragments.quran.quranTfseer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.data.models.quran.Aya
import com.abdallah.prayertimequran.data.models.quran.Tfseer
import com.abdallah.prayertimequran.databinding.CustomSearchQuranBinding

class TfseerAdapter : Adapter<TfseerAdapter.TfseerViewHolder>() {

    var list:List<Tfseer> = ArrayList()
    var listAya:List<Aya> = ArrayList()

    inner class TfseerViewHolder(view : View) : ViewHolder(view){
        val binding = CustomSearchQuranBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TfseerViewHolder {
        return TfseerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_search_quran, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listAya.size
    }

    override fun onBindViewHolder(holder: TfseerViewHolder, position: Int) {
        val tfseerAya = list[position]
        val pageDetails = listAya[position]
       holder.binding.customSearchAya.text =  tfseerAya.text
        holder.binding.customSearchAyaNm.text = pageDetails.aya_text
        holder.binding.customSearchSora.text =
            "سوة "+pageDetails.sora_name_ar+" آيه رقم :  "+pageDetails.aya_no.toString()
    }
}
