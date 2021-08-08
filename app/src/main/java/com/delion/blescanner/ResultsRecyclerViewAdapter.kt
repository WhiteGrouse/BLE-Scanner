package com.delion.blescanner

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.SystemClock
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner

import com.delion.blescanner.databinding.FragmentResultsItemBinding

class ResultsRecyclerViewAdapter(
    private val values: List<DeviceEntry>
) : RecyclerView.Adapter<ResultsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentResultsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.addressView.text = item.address
        val elapsedSeconds = (SystemClock.elapsedRealtimeNanos() - item.timestamp) / 1000000000
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        holder.elapsedView.text = "%02d:%02d".format(minutes, seconds)
        holder.rssiView.text = item.rssi.toString()
    }

    override fun getItemCount(): Int = values.size

    override fun getItemId(position: Int): Long {
        return values[position].address.replace(":", "").toLong(16)
    }

    inner class ViewHolder(binding: FragmentResultsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val addressView: TextView = binding.address
        val elapsedView: TextView = binding.elapsed
        val rssiView: TextView = binding.rssi

        override fun toString(): String {
            return super.toString() + " '" + addressView.text + "'"
        }
    }

}