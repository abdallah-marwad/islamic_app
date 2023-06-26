package com.abdallah.prayertimequran.ui.fragments.azkar.home


import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.common.Constants
import com.abdallah.prayertimequran.data.models.azkar.AzkarDetails
import com.abdallah.prayertimequran.data.models.azkar.AzkarType
import com.abdallah.prayertimequran.databinding.FragmentAzkarBinding
import kotlinx.coroutines.*
import java.util.stream.Collectors

class AzkarFragment : androidx.fragment.app.Fragment() {
    private lateinit var binding: FragmentAzkarBinding
    private lateinit var viewModel: AzkarHomeVieModel
    private lateinit var adapter: AzkarHomeAdapter
    private lateinit var azkarArrayList: ArrayList<AzkarType>
    private var azkarLiveData: MutableLiveData<HashSet<AzkarType>>? = null
    private var coroutineScope: CoroutineScope? = null
    private var job: Job? = null
    var changeText = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAzkarBinding.inflate(layoutInflater)


        viewModel = ViewModelProvider(this)[AzkarHomeVieModel::class.java]


        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.appbar_light_green)
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
        azkarLiveData?.observe(requireActivity()) { zekrType ->

            azkarArrayList = zekrType.stream().collect(Collectors.toList()) as ArrayList<AzkarType>
            if (changeText) {
                Log.d("test", "getAzkarTypes frist time")

                adapter.list = ArrayList(azkarArrayList.sortedBy { it.zekrName })
                binding.azkarRecycler.scheduleLayoutAnimation()
//                changeText = false
            }
            searchInArrayList(azkarArrayList)
        }
    }

    private fun searchInArrayList(arrayList: ArrayList<AzkarType>) {
        binding.azkarFragSearch.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                Log.d("test", "addTextChangedListener")
                prepareTextToSearch(arrayList, it)
            }

        }
        binding.azkarFragSearch.setOnEditorActionListener { txt, id, event ->
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                prepareTextToSearch(arrayList, txt.editableText)
            }
            true
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


    private fun prepareTextToSearch(
        arrayList: ArrayList<AzkarType>,
        p: Editable?
    ) {

        if (p.toString().isNotEmpty()) {
            job?.cancel()
            job = coroutineScope?.launch {
                delay(Constants.SEARCH_DELAY)
                val newArray =
                    arrayList.filter { zekrType1 -> zekrType1.zekrName.contains(p.toString()) } as ArrayList<AzkarType>
                withContext(Dispatchers.Main) {
                    adapter.list = newArray
                    binding.azkarRecycler.scheduleLayoutAnimation()


                }
            }
        } else {
            adapter.list = ArrayList(azkarArrayList.sortedBy { it.zekrName })
            binding.azkarRecycler.scheduleLayoutAnimation()


        }
    }

    override fun onDetach() {
        super.onDetach()
        azkarLiveData = null
        coroutineScope = null
    }


}