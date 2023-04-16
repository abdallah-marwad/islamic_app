package com.example.prayertimequran.ui.fragments.azkar.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayertimequran.R
import com.example.prayertimequran.common.Constants
import com.example.prayertimequran.data.models.azkar.AzkarType
import com.example.prayertimequran.data.models.quran.Aya
import com.example.prayertimequran.databinding.FragmentAzkarBinding
import kotlinx.coroutines.*
import java.util.stream.Collectors

class AzkarFragment : Fragment() {
    private lateinit var binding: com.example.prayertimequran.databinding.FragmentAzkarBinding
    private lateinit var viewModel: AzkarHomeVieModel
    private lateinit var adapter: AzkarHomeAdapter
    private lateinit var azkarArrayList: ArrayList<AzkarType>
    private var azkarLiveData: MutableLiveData<HashSet<AzkarType>>? = null
    private var coroutineScope: CoroutineScope? = null
    private var changeText = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAzkarBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AzkarHomeVieModel::class.java]
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.appbar_light_green)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.customShimmerAzkar.startShimmer()

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
        viewModel.getAzkarWithType(requireContext())
        azkarLiveData = viewModel.azkarLiveData
        coroutineScope = viewModel.coroutineScope
    }

    private fun getAzkarTypes() {
        azkarLiveData?.observe(requireActivity()) {
//            binding.customShimmerAzkar.stopShimmer()
//            binding.customShimmerAzkar.visibility = View.GONE
            azkarArrayList = it.stream().collect(Collectors.toList()) as ArrayList<AzkarType>
            if (changeText) {
                adapter.list = azkarArrayList
                binding.azkarRecycler.scheduleLayoutAnimation()
                changeText = false
            }
            searchInArrayList(azkarArrayList)
        }
    }

    private fun zkerOnClick() {
        adapter.adapterOnClick = {
            Log.d("test", it)
            findNavController().navigate(
                AzkarFragmentDirections.actionAzkarFragmentToAzkarDetailsFragment(it)

            )

        }
    }

    private fun searchInArrayList(arrayList: ArrayList<AzkarType>) {
        binding.azkarFragSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                prepareTextToSearch(arrayList , p1 = p0)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                prepareTextToSearch(arrayList, p1 = p0)

            }

            override fun afterTextChanged(p0: Editable?) {
//                prepareTextToSearch(arrayList , p0)
            }

        }
        )

    }


    private fun prepareTextToSearch(
        arrayList: ArrayList<AzkarType>,
        p0: Editable? = null,
        p1: CharSequence? = null
    ) {
        var p: Any?
        p = if (p0 == null && p1 != null) {
            p1
        } else {
            p0
        }
        if (p.toString().isNotEmpty()) {
            coroutineScope?.launch {
                delay(Constants.SEARCH_DELAY)
                val newArray =
                    arrayList.filter { zekrType1 -> zekrType1.zekrName.contains(p.toString()) } as ArrayList<AzkarType>
                withContext(Dispatchers.Main) {
                    adapter.list = newArray
                    binding.azkarRecycler.scheduleLayoutAnimation()


                }
            }
        } else {
            adapter.list = azkarArrayList
            binding.azkarRecycler.scheduleLayoutAnimation()


        }
    }

    override fun onDetach() {
        super.onDetach()
        azkarLiveData = null
        coroutineScope = null
    }


}