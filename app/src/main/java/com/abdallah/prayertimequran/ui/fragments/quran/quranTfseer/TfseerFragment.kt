package com.abdallah.prayertimequran.ui.fragments.quran.quranTfseer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallah.prayertimequran.R
import com.abdallah.prayertimequran.data.models.quran.Aya
import com.abdallah.prayertimequran.data.models.quran.Tfseer
import com.abdallah.prayertimequran.databinding.FragmentTfseerBinding
import kotlinx.coroutines.*

class TfseerFragment : Fragment() {
    private lateinit var binding: FragmentTfseerBinding
    private lateinit var viewModel: TfseerViewModel
    private lateinit var adapter: TfseerAdapter
    private val tfseerList = ArrayList<Tfseer>()
    private val tfseerListTest = ArrayList<String>()
    val tfseerLiveDataTest = MutableLiveData<String>()

    val args: TfseerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTfseerBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[TfseerViewModel::class.java]
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.appbar_light_green)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("test", "on onViewCreated tfseer")
//        testLiveData()
        backBtnOnclick()
        setUpRv()
        getPageDataFromDB()
    }

    private fun getPageDataFromDB() {
        viewModel.getTfseerAyaByPage(args.pageNum.toInt() + 1)
            .observe(viewLifecycleOwner) {
                getTfseerAya(it)
                registerTfseerAyaCallBack()
            }
    }

//    private fun testLiveData(){
//        for(x in 1..5) {
//            testPostValue(x)
//                tfseerLiveDataTest.observe(viewLifecycleOwner) {
//                    tfseerListTest.add(it)
//                    Log.d("test", tfseerListTest[x])
//            }
//        }
//        Log.d("test", tfseerListTest.size.toString())
//    }
//
//    private fun testPostValue(value : Int){
//            tfseerLiveDataTest.value = "abdo : $value"
//
//    }
    private fun getTfseerAya(list: List<Aya>) {
        viewModel.startWork = {
                list.forEach { aya ->
                    viewModel.getTfseerByPage(
                        aya.sora.toString(),
                        aya.aya_no.toString()
                    )
                }
            getTfseerDataFromViewModel(list)

        }
    }
        private fun registerTfseerAyaCallBack(){
            viewModel.tfseerCallback= {
                tfseerList.add(it)
            }
        }
        private fun getTfseerDataFromViewModel(list: List<Aya>) {
        CoroutineScope(Dispatchers.Main).launch {
                if (tfseerList.size == list.size) {
                    binding.tfseerLottieLoading.visibility = View.GONE
                    adapter.listAya = list
                    adapter.list = tfseerList
                    adapter.notifyDataSetChanged()
                    binding.fragTfseerRv.scheduleLayoutAnimation()

            }
        }

    }

    private fun setUpRv() {
        adapter = TfseerAdapter()
        binding.fragTfseerRv.apply {
            adapter = this@TfseerFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }

    private fun backBtnOnclick() {
        binding.tfseerFragAppbar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("test", "on detatch tfseer")
    }
}
