package com.example.prayertimequran.ui.fragments.azkar.details

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayertimequran.R
import com.example.prayertimequran.data.models.azkar.AzkarDetails
import com.example.prayertimequran.databinding.FragmentAzkarDetailsBinding
import kotlinx.coroutines.CoroutineScope

class AzkarDetailsFragment : Fragment() {
    val args: AzkarDetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentAzkarDetailsBinding
    private var presenter: AzkarDetailsPresenter? = null
    private lateinit var adapter: AzkarDetailsAdapter
    private var azkarLiveData: MutableLiveData<ArrayList<AzkarDetails>>? = null
    private var coroutineScope: CoroutineScope? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAzkarDetailsBinding.inflate(layoutInflater)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.appbar_light_green)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitleOfPage()
        setUpRv()
        backBtnOnclick()
        setUpPresenter()
        getAzkarDetails()
    }

    private fun setUpRv() {
        adapter = AzkarDetailsAdapter()
        binding.azkarFragDetailsRv.adapter = adapter
        binding.azkarFragDetailsRv.layoutManager = LinearLayoutManager(activity)

    }

    private fun setTitleOfPage() {
        binding.appbar.tfseerFragAppbar.text = args.TheNameOfZekr
    }


    private fun setUpPresenter() {
        presenter = AzkarDetailsPresenter(requireContext(), args.TheNameOfZekr)
        Log.d("test", args.TheNameOfZekr)
        presenter?.getAzkarByType()
        azkarLiveData = presenter?.azkarLiveData
        coroutineScope = presenter?.coroutineScope

    }

    private fun getAzkarDetails() {
        azkarLiveData?.observe(requireActivity()) {
            adapter.azkarDetails = it
            binding.azkarFragDetailsRv.scheduleLayoutAnimation()

            adapterOnClick()
        }
    }

    private fun adapterOnClick() {
        adapter.adapterOnClick={
            val clipboard: ClipboardManager = requireContext().getSystemService(CLIPBOARD_SERVICE)
                    as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("تم النسخ", it)
            clipboard.setPrimaryClip(clip)
        }
    }
    private fun backBtnOnclick(){
        binding.appbar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDetach() {
        super.onDetach()
        azkarLiveData = null
        coroutineScope = null
        presenter = null
    }
}