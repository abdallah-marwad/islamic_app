package com.example.prayertimequran.ui.fragments.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayertimequran.R
import com.example.prayertimequran.data.models.azkar.AzkarDetails
import com.example.prayertimequran.data.models.azkar.AzkarType
import com.example.prayertimequran.databinding.FragmentAzkarBinding
import com.example.prayertimequran.databinding.FragmentAzkarDetailsBinding
import com.example.prayertimequran.ui.fragments.azkarHome.home.AzkarHomeAdapter
import com.example.prayertimequran.ui.fragments.azkarHome.home.AzkarHomePresenter
import kotlinx.coroutines.CoroutineScope
import java.util.stream.Collectors

class AzkarDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAzkarDetailsBinding
    private var presenter: AzkarDetailsPresenter? = null
    private lateinit var adapter: AzkarDetailsAdapter
    private var azkarLiveData: MutableLiveData<ArrayList<AzkarDetails>>? = null
    private  var coroutineScope: CoroutineScope? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAzkarDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRv()
        setUpPresenter()
        getAzkarDetails()
    }
    private fun setUpRv() {
        adapter = AzkarDetailsAdapter()
        binding.azkarFragDetailsRv.adapter = adapter
        binding.azkarFragDetailsRv.layoutManager = LinearLayoutManager(activity)
    }

    private fun setUpPresenter() {
        presenter = AzkarDetailsPresenter(requireContext())
        presenter?.getAzkarByType()
        azkarLiveData = presenter?.azkarLiveData
        coroutineScope = presenter?.coroutineScope
    }
    private fun getAzkarDetails() {
        azkarLiveData?.observe(requireActivity()) {
            adapter.azkarDetails = it
        }
    }
    override fun onDetach() {
        super.onDetach()
        azkarLiveData = null
        coroutineScope = null
        presenter = null
    }
}