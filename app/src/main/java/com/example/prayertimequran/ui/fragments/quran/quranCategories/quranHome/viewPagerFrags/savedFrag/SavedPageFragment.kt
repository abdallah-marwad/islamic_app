package com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.savedFrag

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayertimequran.R
import com.example.prayertimequran.data.models.quran.SavedPage
import com.example.prayertimequran.databinding.FragmentQuranPageContainerBinding
import com.example.prayertimequran.databinding.FragmentSavedPageBinding
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters.SavedAdapter
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters.SoraaAdapter
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.juzzFrag.JuzzFragment
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel.SavedPageViewModel
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel.SoraaViewModel
import com.example.prayertimequran.ui.fragments.quran.quranPages.quranPageContainer.QuranPageContainerFragment

class SavedPageFragment : Fragment() {
    private lateinit var binding: FragmentSavedPageBinding
    private lateinit var viewModel: SavedPageViewModel
    private lateinit var adapter: SavedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedPageBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SavedPageViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.customShimmer.startShimmer()
        setUpRv()
        getDataFromViewModel()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getDataFromViewModel(){
        viewModel.savedPagesList!!.observe(requireActivity()){
            binding.customShimmer.stopShimmer()
            binding.customShimmer.visibility = View.GONE
            adapter.list =it as ArrayList<SavedPage>
            binding.savedRv.scheduleLayoutAnimation()
            adapterOnclick()
        }

    }
    private fun setUpRv(){
        adapter = SavedAdapter()
        binding.savedRv.apply {
            adapter = this@SavedPageFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }
    private fun adapterOnclick(){
        adapter.adapterOnClick = {
            JuzzFragment.onJuzzClicked?.let { it1 -> it1(it-1) }
        }
    }


}