package com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.juzzFrag

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallah.prayertimequran.databinding.FragmentJuzzBinding
import com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters.JuzzAdapter
import com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel.JuzzViewModel
import com.abdallah.prayertimequran.ui.fragments.quran.quranPages.quranPageContainer.QuranPageContainerFragment


class JuzzFragment : Fragment() {
    private lateinit var binding: FragmentJuzzBinding
    private lateinit var viewModel: JuzzViewModel
    private lateinit var adapter: JuzzAdapter


    companion object {
        var onJuzzClicked : ((Int)->Unit)? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJuzzBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[JuzzViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.customShimmer.startShimmer()

        setUpRv()
        getDataFromViewModel()
    }

    private fun setUpRv() {
        adapter = JuzzAdapter()
        binding.fragJuzzRv.apply {
            adapter = this@JuzzFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }

    //    private fun setUpViewModel(){
//        coroutineScope = viewModel.coroutineScope
//    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getDataFromViewModel() {
        viewModel.liveDate.observe(requireActivity()) {
            binding.customShimmer.stopShimmer()
            binding.customShimmer.visibility = View.GONE
            adapter.list = it
            binding.fragJuzzRv.scheduleLayoutAnimation()
            adapterOnclick()
        }

    }

    private fun adapterOnclick() {
        adapter.adapterOnClick = {
            onJuzzClicked?.let {
                    it1 -> it1(it-1)
                QuranPageContainerFragment.isPageChangedFromCategories = true
            }
        }
    }



}