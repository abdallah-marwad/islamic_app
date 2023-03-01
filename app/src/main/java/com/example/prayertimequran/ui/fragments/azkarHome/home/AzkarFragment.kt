package com.example.prayertimequran.ui.fragments.azkarHome.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.data.models.azkar.AzkarType
import com.example.prayertimequran.databinding.FragmentAzkarBinding
import kotlinx.coroutines.*
import java.util.stream.Collectors

class AzkarFragment : Fragment() {
    private lateinit var binding: FragmentAzkarBinding
    private var presenter: AzkarHomePresenter? = null
    private lateinit var adapter: AzkarHomeAdapter
    private lateinit var azkarArrayList: ArrayList<AzkarType>
    private var azkarLiveData: MutableLiveData<HashSet<AzkarType>>? = null
    private  var coroutineScope: CoroutineScope? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAzkarBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRv()
        setUpPresenter()
        getAzkarTypes()
        zkerOnClick()
    }

    private fun setUpRv() {
        adapter = AzkarHomeAdapter()
        binding.azkarRecycler.adapter = adapter
        binding.azkarRecycler.layoutManager = LinearLayoutManager(activity)
    }

    private fun setUpPresenter() {
        presenter = AzkarHomePresenter(requireContext())
        presenter?.getAzkarWithType()
        azkarLiveData = presenter?.azkarLiveData
        coroutineScope = presenter?.coroutineScope
    }

    private fun getAzkarTypes() {
        azkarLiveData?.observe(requireActivity()) {
            azkarArrayList = it.stream().collect(Collectors.toList()) as ArrayList<AzkarType>
            adapter.azkarTypes = azkarArrayList
            searchInArrayList(azkarArrayList)
            Log.d("test", azkarArrayList.size.toString())
        }
    }

    private fun zkerOnClick(){
        adapter.adapterOnClick = {
//            findNavController().navigate(
//
//            )
        }
    }
    private fun searchInArrayList(arrayList: ArrayList<AzkarType>){
        binding.azkarFragSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()){
                    coroutineScope?.launch {
                        delay(Constants.SEARCH_DELAY)
                        val newArray =  arrayList.filter { zekrType1 -> zekrType1.zekrName.contains(p0.toString()) } as ArrayList<AzkarType>
                        withContext(Dispatchers.Main) {
                            adapter.azkarTypes = newArray
                        }
                    }
                }
                else{
                    adapter.azkarTypes = azkarArrayList

                }
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        azkarLiveData = null
        coroutineScope = null
        presenter = null
    }


}