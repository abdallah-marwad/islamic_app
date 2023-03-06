package com.example.prayertimequran.ui.fragments.azkarHome.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayertimequran.R
import com.example.prayertimequran.data.models.azkar.AzkarDetails
import com.example.prayertimequran.databinding.FragmentAzkarDetailsBinding

import kotlinx.coroutines.CoroutineScope

class AzkarDetailsFragment : Fragment() {
    val args: AzkarDetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentAzkarDetailsBinding
    private var presenter: AzkarDetailsPresenter? = null
    private lateinit var adapter: AzkarDetailsAdapter
    private var azkarLiveData: MutableLiveData<ArrayList<AzkarDetails>>? = null
    private var coroutineScope: CoroutineScope? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAzkarDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitleOfPage()
        setUpRv()
        setUpPresenter()
        getAzkarDetails()
    }

    private fun setUpRv() {
        adapter = AzkarDetailsAdapter()
        binding.azkarFragDetailsRv.adapter = adapter
        binding.azkarFragDetailsRv.layoutManager = LinearLayoutManager(activity)

    }

    private fun setTitleOfPage() {
        binding.azkarFragDetailsCategory.text = args.TheNameOfZekr
    }


    private fun setUpPresenter() {
        presenter = AzkarDetailsPresenter(requireContext(), args.TheNameOfZekr)
        Log.d("test", args.TheNameOfZekr)
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