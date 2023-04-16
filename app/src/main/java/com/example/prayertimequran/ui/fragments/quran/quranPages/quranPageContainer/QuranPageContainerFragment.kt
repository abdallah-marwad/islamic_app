package com.example.prayertimequran.ui.fragments.quran.quranPages.quranPageContainer

import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.prayertimequran.R
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.databinding.FragmentPageQuranBinding
import com.example.prayertimequran.databinding.FragmentQuranContainer2Binding
import com.example.prayertimequran.databinding.FragmentQuranPageContainerBinding
import com.example.prayertimequran.ui.fragments.quran.quranPages.quranPagePic.QuranPageFragment
import kotlinx.coroutines.CoroutineScope


class QuranPageContainerFragment() : Fragment() {
    private lateinit var binding: FragmentQuranPageContainerBinding
    private lateinit var quranPageContainerViewModel: QuranPageContainerViewModel
    var coroutineScope: CoroutineScope? = null
    val args: QuranPageContainerFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuranPageContainerBinding.inflate(layoutInflater)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.gray_icons)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
        setUpPager()
        fehrsOnClick()

    }

    companion object {
        var fromQuranCategories = false
        var fromPageContainerToCategoryContainer = false

    }

    private fun setUpViewModel() {
        quranPageContainerViewModel =
            ViewModelProvider(this)[QuranPageContainerViewModel::class.java]
        coroutineScope = quranPageContainerViewModel.coroutineScope
        quranPageContainerViewModel.context = requireContext().applicationContext as Application
        quranPageContainerViewModel.frag = requireActivity()

    }

    private fun setUpPager() {
        binding.quranPager.adapter = quranPageContainerViewModel.getMyAdapter()
        val num = setPageNumber()
        binding.quranPager.setCurrentItem(num, false)
    }

    private fun setPageNumber(): Int {
        return if (fromQuranCategories) {
            fromQuranCategories = false
            args.currentPageNumber

        } else {
            quranPageContainerViewModel.getSharedCurrentItem()
        }

    }

    private fun fehrsOnClick() {
        QuranPageFragment.onQuranPageClicked = {

            if (it == Constants.FEHRES) {
                navigateToQuranCategories(0)
            } else if (it == Constants.JUZZ) {
                navigateToQuranCategories(1)
            } else if (it == Constants.SAVED) {
                navigateToQuranCategories(2)
            } else if (it == Constants.TFSEER) {
                view?.post {
                    findNavController().navigate(
                        QuranPageContainerFragmentDirections.actionQuranPageContainerFragment2ToTfseerFragment(
                            binding.quranPager.currentItem.toString()
                        )
                    )

                }
            } else if (it == Constants.SEARCH) {
                findNavController().navigate(QuranPageContainerFragmentDirections.
                actionQuranPageContainerFragment2ToQuranSearchFragment())
            }


        }
    }

    private fun navigateToQuranCategories(pageNum: Int) {
        fromPageContainerToCategoryContainer = true
        view?.post {
            findNavController().navigate(
                QuranPageContainerFragmentDirections.actionQuranPageContainerFragment2ToQuranContainerFragment(
                    pageNum
                )
            )
        }
    }

    override fun onPause() {
        super.onPause()
        quranPageContainerViewModel.writeInShared(binding.quranPager.currentItem)

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDetach() {
        super.onDetach()
        coroutineScope = null
    }

}