package com.abdallah.prayertimequran.ui.fragments.quran.searchFragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.data.models.quran.Aya
import com.abdallah.prayertimequran.databinding.FragmentQuranSearch2Binding
import com.abdallah.prayertimequran.ui.fragments.quran.quranPages.quranPageContainer.QuranPageContainerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class QuranSearchFragment : Fragment() {
    private lateinit var binding: FragmentQuranSearch2Binding
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter
    private  var job :Job?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuranSearch2Binding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.appbar_light_green)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRv()
        openKeyboard()
        setTitleOfPage()
        backBtnOnclick()
        edittextTypingListener()
        adapterOnclick()

    }

    private fun backBtnOnclick() {
        binding.appbar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun openKeyboard() {
        binding.fragSearchEt.post {
            val keyboard: InputMethodManager? =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            keyboard!!.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
            binding.fragSearchEt.requestFocus()
        }
    }

    private fun setUpRv() {
        adapter = SearchAdapter()
        binding.fragSearchRv.apply {
            adapter = this@QuranSearchFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }

    private fun setTitleOfPage() {
        binding.appbar.tfseerFragAppbar.text = "البحث بالآيه"
    }

    private fun edittextTypingListener() {
        binding.fragSearchEt.addTextChangedListener {

            prepareTextToSearch(it)
        }
        binding.fragSearchEt.setOnEditorActionListener {txt , id,event->
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                prepareTextToSearch(txt.editableText)
            }
            true
        }
    }

    private fun prepareTextToSearch(p: Editable?  ) {
        job?.cancel()
        job = viewModel.viewModelScope.launch {
            delay(Constants.SEARCH_DELAY)
            if (p!!.isNotEmpty() && p.length >1) {
                viewModel.getSearchData(p.toString())?.observe(requireActivity()) {
                    adapter.list = it as ArrayList<Aya>
                    adapter.notifyDataSetChanged()
                    binding.fragSearchRv.scheduleLayoutAnimation()

                }
            } else {
                adapter.list = null
                adapter.notifyDataSetChanged()
                binding.fragSearchRv.scheduleLayoutAnimation()

            }
        }
    }

    private fun adapterOnclick() {
        adapter.adapterOnClick = {
            QuranPageContainerFragment.fromQuranCategories = true
            findNavController().navigate(
                QuranSearchFragmentDirections.actionQuranSearchFragmentToQuranPageContainerFragment23(
                    it
                )
            )


        }
    }
}