package com.example.prayertimequran.ui.fragments.quran.searchFragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.data.models.quran.Aya
import com.example.prayertimequran.databinding.FragmentQuranSearch2Binding
import com.example.prayertimequran.ui.fragments.quran.quranPages.quranPageContainer.QuranPageContainerFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class QuranSearchFragment : Fragment() {

    private lateinit var binding: FragmentQuranSearch2Binding
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = FragmentQuranSearch2Binding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
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
    private fun backBtnOnclick(){
        binding.appbar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }
private fun openKeyboard(){
    binding.fragSearchEt.post{
        val keyboard: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        keyboard!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
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
    private fun edittextTypingListener(){
        binding.fragSearchEt.addTextChangedListener (object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                prepareTextToSearch(p1 = p0)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                prepareTextToSearch(p0 )


            }

        })
    }

    private fun prepareTextToSearch(p0 : Editable? = null , p1 : CharSequence? = null){
        var p :Any?= Any()
        p = if(p0 == null&& p1 != null){
            p1
        }else{
            p0
        }
        if(p!!.isNotEmpty()) {
            viewModel.viewModelScope.launch {
                delay(Constants.SEARCH_DELAY)
                viewModel.getSearchData(p.toString())?.observe(requireActivity()) {
                    adapter.list = it as ArrayList<Aya>
                    adapter.notifyDataSetChanged()
                    binding.fragSearchRv.scheduleLayoutAnimation()

                }
            }
        }else{
            adapter.list = null
            adapter.notifyDataSetChanged()
            binding.fragSearchRv.scheduleLayoutAnimation()

        }
    }
    private fun adapterOnclick() {
        adapter.adapterOnClick = {
            QuranPageContainerFragment.fromQuranCategories = true
            findNavController().navigate(
                QuranSearchFragmentDirections.actionQuranSearchFragmentToQuranPageContainerFragment23(it)
            )


        }
    }
}