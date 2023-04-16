package com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.juzzFrag.JuzzFragment
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.savedFrag.SavedPageFragment
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.soraa.SoraaFragment

class PagerQuranHomeAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SoraaFragment()
            1 -> JuzzFragment()
            else -> SavedPageFragment()

        }
    }
}