package com.abdallah.prayertimequran.ui.fragments.quran.quranPages.quranPageContainer

import android.app.Application
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
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.databinding.FragmentQuranPageContainerBinding
import com.abdallah.prayertimequran.ui.fragments.quran.quranPages.quranPagePic.QuranPageFragment
import kotlinx.coroutines.CoroutineScope


class QuranPageContainerFragment : Fragment() {
    private lateinit var binding: FragmentQuranPageContainerBinding
    private lateinit var quranPageContainerViewModel: QuranPageContainerViewModel
    var coroutineScope: CoroutineScope? = null
    val args: QuranPageContainerFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuranPageContainerBinding.inflate(layoutInflater)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.quran_appbar)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("test","onViewCreated QuranPageContainer.class")

        setUpViewModel()
        binding.quranPager.adapter = quranPageContainerViewModel.getMyAdapter()
        setUpPager()
        fehrsOnClick()

    }

    companion object {
        var fromQuranCategories = false
        var fromPageContainerToCategoryContainer = false
        var isPageChangedFromCategories = false

    }


    private fun setUpViewModel() {
        quranPageContainerViewModel =
            ViewModelProvider(this)[QuranPageContainerViewModel::class.java]
        coroutineScope = quranPageContainerViewModel.coroutineScope
        quranPageContainerViewModel.context = requireContext().applicationContext as Application
        quranPageContainerViewModel.frag = requireActivity()

    }

    private fun setUpPager() {
        val num = setPageNumber()
        binding.quranPager.setCurrentItem(num, false)
    }

    private fun setPageNumber(): Int {
        Log.d("test" , "fromQuranCategories is $fromQuranCategories")
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
                findNavController().navigate(
                    QuranPageContainerFragmentDirections.actionQuranPageContainerFragment2ToQuranSearchFragment()
                )
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
        Log.d("test","onPause and save current page QuranPageContainer.class")

    }

    override fun onResume() {
        super.onResume()
        Log.d("test","onResume QuranPageContainer.class")
//        if (!isPageChangedFromCategories) {
//            setUpPager()
//            Log.d("test","onResume page changed from  categories QuranPageContainer.class")
//
//            isPageChangedFromCategories = true
//        }
    }

    override fun onDetach() {
        super.onDetach()
        coroutineScope = null
    }

}