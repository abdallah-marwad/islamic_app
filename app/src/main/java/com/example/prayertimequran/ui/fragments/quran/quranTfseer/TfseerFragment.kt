package com.example.prayertimequran.ui.fragments.quran.quranTfseer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayertimequran.R
import com.example.prayertimequran.data.models.quran.Aya
import com.example.prayertimequran.data.models.quran.Tfseer
import com.example.prayertimequran.databinding.FragmentSoraaBinding
import com.example.prayertimequran.databinding.FragmentTfseerBinding
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.adapters.SoraaAdapter
import com.example.prayertimequran.ui.fragments.quran.quranCategories.quranHome.viewPagerFrags.viewmodel.SoraaViewModel

class TfseerFragment : Fragment() {
    private lateinit var binding: FragmentTfseerBinding
    private lateinit var viewModel: TfseerViewModel
    private lateinit var adapter: TfseerAdapter
    private val tfseerList = ArrayList<Tfseer>()
    val args: TfseerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTfseerBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[TfseerViewModel::class.java]
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.appbar_light_green)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backBtnOnclick()
        setUpRv()
        getPageDataFromDB()
    }

    private fun getPageDataFromDB() {
        viewModel.getTfseerAyaByPage(args.pageNum.toInt() + 1).observe(requireActivity()) {
            getTfseerAya(it)
        }
    }


    private fun getTfseerAya(list: List<Aya>) {

        list.forEach { aya ->
            viewModel.getTfseerByPage(
                aya.sora.toString(),
                aya.aya_no.toString()
            )
            getTfseerDataFromViewModel(list)
        }
    }

    private fun getTfseerDataFromViewModel(list: List<Aya>) {
        viewModel.tfseerLiveData.observe(requireActivity()) {
            tfseerList.add(it)
            Log.d("test", "tfseerList.size = ${tfseerList.size}  list.size = ${list.size}")
            if (tfseerList.size == list.size) {
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

private fun backBtnOnclick(){
    binding.tfseerFragAppbar.back.setOnClickListener {
        findNavController().popBackStack()
    }
}
}
