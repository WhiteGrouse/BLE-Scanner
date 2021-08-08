package com.delion.blescanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log

class Scanner(val adapter: BluetoothAdapter) {
    private var scanning = false

    private val _devices: MutableList<DeviceEntry> = ArrayList()
    val devices: List<DeviceEntry> get() = _devices

    var onDeviceAdded: ((position: Int) -> Unit)? = null
    var onDeviceUpdated: ((position: Int) -> Unit)? = null
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            if(result == null) return
            val address = result.device.address
            var pos = devices.indexOfFirst { it.address == address }
            if(pos == -1) {
                pos = _devices.size
                _devices.add(DeviceEntry(address, result.timestampNanos, result.rssi))
                onDeviceAdded?.invoke(pos)
            }
            else {
                with(_devices[pos]) {
                    timestamp = result.timestampNanos
                    rssi = result.rssi
                }
                onDeviceUpdated?.invoke(pos)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("BLE Scanner", "Scan failed..............($errorCode)")
        }
    }

    init {
        if(adapter.isEnabled) {
            startScan()
        }
    }

    fun onStateChanged(state: Int) {
        when(state) {
            BluetoothAdapter.STATE_ON -> {
                startScan()
            }
            BluetoothAdapter.STATE_TURNING_OFF -> {
                stopScan()
            }
        }
    }

    private fun startScan() {
        if(!adapter.isEnabled) return
        if(scanning) {
            stopScan()
        }

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setLegacy(false)
            .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
            .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
            .build()
        adapter.bluetoothLeScanner.startScan(emptyList(), scanSettings, scanCallback)
        scanning = true
        Log.d("BLE Scanner", "Start scan")
    }

    private fun stopScan() {
        if (!adapter.isEnabled) return
        adapter.bluetoothLeScanner.stopScan(scanCallback)
        scanning = false
        Log.d("BLE Scanner", "Stop scan")
    }
}