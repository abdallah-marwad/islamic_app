package com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.soraa

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallah.prayertimequran.databinding.FragmentSoraaBinding
import com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters.SoraaAdapter
import com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.juzzFrag.JuzzFragment
import com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel.SoraaViewModel
import com.abdallah.prayertimequran.ui.fragments.quran.quranPages.quranPageContainer.QuranPageContainerFragment.Companion.isPageChangedFromCategories


class SoraaFragment : Fragment() {
    private lateinit var binding: FragmentSoraaBinding
    private lateinit var viewModel: SoraaViewModel
    private lateinit var adapter: SoraaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSoraaBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[SoraaViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.customShimmer.startShimmer()
        setUpRv()
        getDataFromViewModel()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataFromViewModel() {
        viewModel.liveDate.observe(requireActivity()) {
            binding.customShimmer.stopShimmer()
            binding.customShimmer.visibility = View.GONE
            adapter.list = it
            binding.soraaFragRv.scheduleLayoutAnimation()
            adapterOnclick()
        }

    }

    private fun setUpRv() {
        adapter = SoraaAdapter()
        binding.soraaFragRv.apply {
            adapter = this@SoraaFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }

    private fun adapterOnclick() {
        adapter.adapterOnClick = {
            JuzzFragment.onJuzzClicked?.let {
                    it1 -> it1(it-1)
                isPageChangedFromCategories = true
            }

        }
    }


}