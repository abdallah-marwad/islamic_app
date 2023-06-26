package com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranCategoryContainer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.databinding.FragmentQuranContainer2Binding
import com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters.PagerQuranHomeAdapter
import com.abdallah.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.juzzFrag.JuzzFragment
import com.abdallah.prayertimequran.ui.fragments.quran.quranPages.quranPageContainer.QuranPageContainerFragment
import com.google.android.material.tabs.TabLayoutMediator


class QuranContainerFragment : Fragment() {
    lateinit var adapter: PagerQuranHomeAdapter
    val args: QuranContainerFragmentArgs by navArgs()

    private lateinit var binding: FragmentQuranContainer2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuranContainer2Binding.inflate(inflater)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.appbar_light_green)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PagerQuranHomeAdapter(requireActivity())
        binding.quranHomeViewpager.adapter = adapter
        if (QuranPageContainerFragment.fromPageContainerToCategoryContainer) {
            binding.quranHomeViewpager.setCurrentItem(args.currentItem, false)
            QuranPageContainerFragment.fromPageContainerToCategoryContainer = false
        }

        setUpTabLayout()
        soraOnClick()
        edittextOnClick()

    }

    private fun setUpTabLayout() {
        TabLayoutMediator(
            binding.fragQuranContainerTablayout,
            binding.quranHomeViewpager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "السور"
                1 -> tab.text = "الأجزاء"
                else -> tab.text = "العلامات المرجعيه"
            }
        }.attach()
    }

    private fun soraOnClick() {
        JuzzFragment.onJuzzClicked = {
            QuranPageContainerFragment.fromQuranCategories = true
            view?.post {
                findNavController().navigate(
                    QuranContainerFragmentDirections.actionQuranContainerFragmentToQuranPageContainerFragment2(
                        it
                    )
                )
            }
        }
    }

    private fun edittextOnClick() {
        binding.quranFragSearch.setOnClickListener {
            findNavController().navigate(
                QuranContainerFragmentDirections.actionQuranContainerFragmentToQuranSearchFragment()                )
        }



    }
}