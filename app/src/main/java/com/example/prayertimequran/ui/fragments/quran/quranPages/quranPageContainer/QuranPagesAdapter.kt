package com.example.prayertimequran.ui.fragments.quran.quranPages.quranPageContainer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.prayertimequran.ui.fragments.quran.quranPages.quranPagePic.QuranPageFragment

class QuranPagesAdapter(fragmentActivity: FragmentActivity ) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return com.example.prayertimequran.common.Constants.PAGES_NUMBER
    }

    override fun createFragment(position: Int): Fragment {
        return QuranPageFragment(1+position)
    }
}