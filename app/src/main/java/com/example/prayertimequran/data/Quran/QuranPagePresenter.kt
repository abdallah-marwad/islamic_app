package com.example.prayertimequran.data.Quran

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.prayertimequran.ui.fragments.quran.quranPage.QuranPagesManager


class QuranPagePresenter(val context : Context) {
    private val quranPagesManager : QuranPagesManager =
        QuranPagesManager()
    fun getQuranImageByPageNumber(pageNumber: Int): Drawable? {
        return quranPagesManager.getQuranImageByPageNumber(context, pageNumber) // err might her
    }
}