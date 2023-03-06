package com.example.prayertimequran.ui.fragments.quran

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.prayertimequran.R
import com.example.prayertimequran.data.Quran.QuranPagePresenter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class QuranFragment : Fragment() {

    private lateinit var img : ImageView
    private lateinit var quranPagePresenter: QuranPagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        quranPagePresenter= QuranPagePresenter(requireContext().applicationContext)
        return inflater.inflate(R.layout.fragment_quran, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        img = view.findViewById(R.id.frag_quran_img)
        img.setImageDrawable(quranPagePresenter.getQuranImageByPageNumber(10))
    }


}