package com.delion.blescanner.view

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.delion.blescanner.R
import com.delion.blescanner.Scanner
import com.delion.blescanner.navigateTo
import com.delion.blescanner.view.fragment.DisabledFragment
import com.delion.blescanner.view.fragment.RequestPermissionsFragment
import com.delion.blescanner.view.fragment.ResultsFragment
import com.delion.blescanner.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent!!.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    when(state) {
                        BluetoothAdapter.STATE_ON -> {
                            navigateTo(supportFragmentManager, ResultsFragment())
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            navigateTo(supportFragmentManager, DisabledFragment())
                        }
                    }
                    viewModel.scanner?.onStateChanged(state)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            navigateTo(supportFragmentManager, RequestPermissionsFragment())
        }
        registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    fun onPermissionGranted() {
        viewModel.scanner = Scanner(BluetoothAdapter.getDefaultAdapter()!!)
        if (viewModel.scanner!!.adapter.isEnabled) {
            navigateTo(supportFragmentManager, ResultsFragment())
        }
        else {
            navigateTo(supportFragmentManager, DisabledFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}