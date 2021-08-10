package com.delion.blescanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.ParcelUuid
import android.util.Log
import java.nio.ByteBuffer

class Scanner(val adapter: BluetoothAdapter) {
    private val serviceUuid = ParcelUuid.fromString("9703e04c-991f-40e5-bff2-2234cc25788b")
    private var scanning = false

    private val _devices: MutableList<DeviceEntry> = ArrayList()
    val devices: List<DeviceEntry> get() = _devices

    var onDeviceAdded: ((position: Int) -> Unit)? = null
    var onDeviceUpdated: ((position: Int) -> Unit)? = null
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            if(result == null) return
            //val address = result.device.address
            val address = ByteBuffer.wrap(result.scanRecord!!.serviceData[serviceUuid]!!).getShort().toString()
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
        val filter = ScanFilter.Builder()
            .setServiceData(serviceUuid, serviceUuid.uuid.toByteArray())
            .build()
        adapter.bluetoothLeScanner.startScan(listOf(filter), scanSettings, scanCallback)
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