package com.delion.blescanner

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels

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