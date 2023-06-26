package com.abdallah.prayertimequran.ui.fragments.quran.quranPages.quranPagePic

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.data.models.quran.SavedPage
import com.abdallah.prayertimequran.databinding.FragmentPageQuranBinding
import kotlinx.coroutines.launch

class QuranPageFragment() : Fragment() {
    private lateinit var quranPageViewModel: QuranPageViewModel
    private lateinit var binding: FragmentPageQuranBinding
    private var pageNumber: Int? = 0
    private var imageClicked = false
    private var isPageAdded = false

    constructor(pageNumber: Int) : this() {
        this.pageNumber = pageNumber
    }

    companion object {
        var onQuranPageClicked: ((String) -> Unit)? = null

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPageQuranBinding.inflate(inflater)
        quranPageViewModel = ViewModelProvider(this)[QuranPageViewModel::class.java]
        Log.d("test" ,"onCreateView  Quran Page" )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragQuranImg.setImageDrawable(
            quranPageViewModel.getQuranImageByPageNumber(
                pageNumber ?: 1
            )
        )

        getSavedPages()
        imageClickHandle()
        fehrsOnClick()
        juzzOnClick()
        savedPagesOnClick()
        searchOnClick()
        tfseerOnClick()
        addingOnClick()
        lottieOnClick()
    }

    private fun imageClickHandle() {
        binding.fragQuranImg.setOnClickListener {
            if (imageClicked) {
                bottomConstraintVisibility(View.INVISIBLE)
                imageClicked = false
            } else {
                bottomConstraintVisibility(View.VISIBLE)
                imageClicked = true
            }
        }
    }

    private fun bottomConstraintVisibility(visibility: Int) {
        binding.bottomConstraintContainer.visibility = visibility
    }

    //
//
    private fun fehrsOnClick() {
        binding.fragQuranSoraa.setOnClickListener {
            onQuranPageClicked.let {
                if (it != null) {
                    it(Constants.FEHRES)
                }
            }
        }
    }

    private fun searchOnClick() {
        binding.fragQuranSearch.setOnClickListener {
            onQuranPageClicked.let {
                if (it != null) {
                    it(Constants.SEARCH)
                }

            }
        }
    }

    private fun juzzOnClick() {
        binding.fragQuranJuzz.setOnClickListener {
            onQuranPageClicked.let {
                if (it != null) {
                    it(Constants.JUZZ)
                }
            }
        }

    }

    private fun savedPagesOnClick() {
        binding.fragQuranSaved.setOnClickListener {
            onQuranPageClicked.let {
                if (it != null) {
                    it(Constants.SAVED)
                }
            }
        }

    }

    private fun tfseerOnClick() {
        binding.fragQuranTafser.setOnClickListener {
            onQuranPageClicked.let {
                if (it != null) {
                    it(Constants.TFSEER)
                }
            }
        }

    }

    private fun addingOnClick() {
        binding.fragQuranAdding.setOnClickListener {
            if (isPageAdded) {
                deleteItem()
            } else {
                addItem()
            }
        }

    }
    private fun lottieOnClick() {
        binding.savedLottie.setOnClickListener {
            if (isPageAdded) {
                deleteItem()
            } else {
                addItem()
            }
        }

    }

    private fun getSavedPages() {
        quranPageViewModel.getSavedPages()
        quranPageViewModel.savedPagesList.observe(viewLifecycleOwner) { savedListLiveDate ->
            savedListLiveDate.forEach {
                if (it?.pageNumber == pageNumber) {
                    isPageAdded = true
                    changeBookMark(0.0f,0.5f, "حذف")


                }

            }

        }
    }

    private fun addItem() {
        quranPageViewModel.addPage(SavedPage(pageNumber!!))
        lifecycleScope.launch {
            quranPageViewModel.mutableState.collect {
                if (it == Constants.ADDING) {
                    isPageAdded = true
                    changeBookMark(0.0f,0.5f, "حذف")
                }
            }
        }
    }

    private fun deleteItem() {
        quranPageViewModel.deletePage(SavedPage(pageNumber!!))
        lifecycleScope.launch {
            quranPageViewModel.mutableState.collect {
                if (it == Constants.DELETE) {
                    isPageAdded = false
                    changeBookMark(0.5f,1.0f, "إضافه",true)
                }
            }
        }
    }


    private fun changeBookMark(minProgress: Float,maxProgress: Float, word: String , changeBackGround : Boolean = false) {
        binding.savedLottie.setMinAndMaxProgress(minProgress, maxProgress)
        binding.savedLottie.playAnimation()



        binding.fragQuranAdding.text = word

    }
}





