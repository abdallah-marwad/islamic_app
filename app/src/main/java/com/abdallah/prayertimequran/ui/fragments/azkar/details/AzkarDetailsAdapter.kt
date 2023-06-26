package com.abdallah.prayertimequran.ui.fragments.azkar.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.data.models.azkar.AzkarDetails
import com.abdallah.prayertimequran.databinding.CustomAzkarDetailsBinding

class AzkarDetailsAdapter : RecyclerView.Adapter<AzkarDetailsAdapter.ZekrDetailsViewHolder>() {

    var azkarDetails: ArrayList<AzkarDetails> = ArrayList()
        set(azkarType) {
            field = azkarType
            notifyDataSetChanged()
        }

    private fun pagesNumber() : ArrayList<Int>{
        val list = ArrayList<Int>()
        for(i in 1..azkarDetails.size) {
            list.add(i)
        }
        return list
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
        val pageNumber = pagesNumber()[position]
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
            holder.binding.customZekrDetailsCount.text = "التكرار : غير مذكور"
        }else{
            holder.binding.customZekrDetailsCount.text = "عدد المرات : "+zekrDetails.count}

        holder.binding.customSzekrNumber.text = pageNumber.toString()
        holder.binding.customZekrDetailsZekr.text = zekrDetails.zekr

        holder.binding.customSzekrCopy.setOnClickListener {
            adapterOnClick?.let { it1 -> it1(zekrDetails.zekr) }
        }

//        holder.binding.calc.setOnClickListener {
//            val num =  calcZekrNumber(zekrDetails.count.toIntOrNull())
//            holder.binding.calc.text = num.toString()
//        }
    }
    private fun calcZekrNumber(num : Int?) = num?:0 -1

    var adapterOnClick: ((String) -> Unit)? = null


}