package com.example.prayertimequran.ui.fragments.quran.quranTfseer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.prayertimequran.R
import com.example.prayertimequran.data.models.quran.Aya
import com.example.prayertimequran.data.models.quran.Tfseer
import com.example.prayertimequran.data.models.quran.TfseerRV
import com.example.prayertimequran.databinding.CustomTfseerBinding

class TfseerAdapter : Adapter<TfseerAdapter.TfseerViewHolder>() {

    var list:List<Tfseer> = ArrayList()
    var listAya:List<Aya> = ArrayList()

    inner class TfseerViewHolder(view : View) : ViewHolder(view){
        val binding = CustomTfseerBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TfseerViewHolder {
        return TfseerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_tfseer, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listAya.size
    }

    override fun onBindViewHolder(holder: TfseerViewHolder, position: Int) {
        val tfseerAya = list[position]
        val pageDetails = listAya[position]
       holder.binding.customTfseerAyaTxt.text = pageDetails.aya_text
        holder.binding.customTfseerTxt.text = tfseerAya.text
        holder.binding.customTfseerSoraDetails.text =
            "سوة "+pageDetails.sora_name_ar+" آيه رقم :  "+pageDetails.aya_no.toString()
    }
}
