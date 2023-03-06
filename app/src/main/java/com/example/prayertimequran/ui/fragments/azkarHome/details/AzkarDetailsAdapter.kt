package com.example.prayertimequran.ui.fragments.azkarHome.details

import android.transition.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prayertimequran.R
import com.example.prayertimequran.data.models.azkar.AzkarDetails
import com.example.prayertimequran.data.models.azkar.AzkarType
import com.example.prayertimequran.databinding.CustomAzkarDetailsBinding
import com.example.prayertimequran.databinding.CustomAzkarTypeBinding

class AzkarDetailsAdapter : RecyclerView.Adapter<AzkarDetailsAdapter.ZekrDetailsViewHolder>() {

    var azkarDetails: ArrayList<AzkarDetails> = ArrayList()
        set(azkarType) {
            field = azkarType
            notifyDataSetChanged()
        }


    inner class ZekrDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding = CustomAzkarDetailsBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZekrDetailsViewHolder {
        return ZekrDetailsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_azkar_details, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return azkarDetails.size
    }

    override fun onBindViewHolder(holder: ZekrDetailsViewHolder, position: Int) {
        val zekrDetails = azkarDetails[position]
        if(zekrDetails.description.isEmpty()){
            holder.binding.customZekrDetailsDescription.visibility =View.GONE
        }else{
            holder.binding.customZekrDetailsDescription.text = zekrDetails.description
        }


        if(zekrDetails.reference.isEmpty()){
            holder.binding.customZekrDetailsRefrence.visibility =View.GONE
        }else{
            holder.binding.customZekrDetailsRefrence.text = "المصدر : "+ zekrDetails.reference
        }
        if(zekrDetails.count.isEmpty()){
            holder.binding.customZekrDetailsCount.visibility =View.GONE
        }else{
            holder.binding.customZekrDetailsCount.text = "التكرار : "+zekrDetails.count}


        holder.binding.customZekrDetailsZekr.text = zekrDetails.zekr

//        holder.binding.root.setOnClickListener {
//            adapterOnClick?.let { it1 -> it1(azkarTypes[position].zekrName) }
//        }
    }

//    var adapterOnClick: ((String) -> Unit)? = null


}