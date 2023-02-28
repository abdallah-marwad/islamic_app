package com.example.prayertimequran.ui.fragments.azkarHome

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayertimequran.R
import com.example.prayertimequran.data.models.azkar.AzkarType
import com.example.prayertimequran.databinding.FragmentAzkarBinding
import java.util.stream.Collector
import java.util.stream.Collectors

class AzkarFragment : Fragment(R.layout.fragment_azkar) {
    private lateinit var binding : FragmentAzkarBinding
    private lateinit var presenter : AzkarHomePresenter
    private lateinit var adapter : AzkarHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAzkarBinding.inflate(layoutInflater)
        setUpPresenter()
        return inflater.inflate(R.layout.fragment_azkar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRv()
        getAzkarTypes()
    }
    private fun setUpRv(){
        adapter = AzkarHomeAdapter()
        binding.azkarRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.azkarRecycler.adapter = adapter

    }
    private fun setUpPresenter(){
        presenter = AzkarHomePresenter(requireContext())
        presenter.getAzkarWithType()
    }
    private fun getAzkarTypes(){
        presenter.azkarLiveData.observe(requireActivity()){
            val azkarArrayList = it.stream().collect(Collectors.toList()) as ArrayList<AzkarType>
            adapter.azkarTypes = azkarArrayList
            Log.d("test" ,azkarArrayList[0].zekrName )
        }
    }

}