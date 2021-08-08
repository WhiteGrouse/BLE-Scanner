package com.delion.blescanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.delion.blescanner.databinding.FragmentResultsBinding
import java.util.*
import kotlin.concurrent.timer

class ResultsFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var listAdapter: ResultsRecyclerViewAdapter
    private lateinit var timer: Timer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentResultsBinding.inflate(inflater, container, false)
        listAdapter = ResultsRecyclerViewAdapter(viewModel.scanner!!.devices)
        listAdapter.setHasStableIds(true)
        viewModel.scanner!!.onDeviceAdded = { position ->
            listAdapter.notifyItemInserted(position)
        }
        viewModel.scanner!!.onDeviceUpdated = { position ->
            listAdapter.notifyItemChanged(position)
        }
        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
            itemAnimator = null
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = Handler(Looper.getMainLooper())
        timer = timer("update_elapsed_timer", period = 1000) {
            handler.post {
                listAdapter.notifyItemRangeChanged(0, listAdapter.itemCount)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.scanner!!.onDeviceAdded = null
        viewModel.scanner!!.onDeviceUpdated = null
        timer.cancel()
    }
}