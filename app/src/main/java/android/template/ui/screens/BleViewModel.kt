/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.ui.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val TAG = "BleViewModel"

@HiltViewModel
class BleViewModel @Inject constructor(
    @ApplicationContext private val context: Context) : ViewModel()
{
    private val bluetoothLeScanner: BluetoothLeScanner
    init {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var bleGatt: BluetoothGatt? = null

    private fun addDevice(device: Device) {
        if (_uiState.value.deviceList.find { it.address == device.address } != null) {
            return
        }
        _uiState.update { state ->
            val newList = state.deviceList.toMutableList()
            newList.add(device)
            state.copy(
                deviceList = newList,
            )
        }
    }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d(TAG, "onScanResult: ${result.device}")
            val record = result.scanRecord ?: return
            Log.d(TAG, "ScanRecord: $result.scanRecord}")
            if (record.deviceName == null) {
                return
            }
            addDevice(
                Device(
                    address = result.device.address,
                    name = record.deviceName!!,
                    ssid = result.rssi,
                    device = result.device,
                    scanRecord = record
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun startDeviceScan() {
        if (!_uiState.value.scanning) {
            _uiState.update {
                it.copy(
                    deviceList = emptyList(),
                    scanning = true
                )
            }
            Log.d(TAG, "onClickScan: start searching")
            bluetoothLeScanner.startScan(scanCallback)
        } else {
            _uiState.update {
                it.copy(scanning = false)
            }
            Log.d(TAG, "onClickScan: stop searching")
            stopScan()
        }
    }

    private fun stopScan(): Boolean {
        if (!_uiState.value.scanning) {
            Log.d(TAG, "not scanning")
            return false
        }
        try {
            bluetoothLeScanner.stopScan(scanCallback)
            _uiState.update {
                it.copy(scanning = false)
            }
        }
        catch (e: SecurityException) {
            Log.e(TAG, "stopScan: $e")
            return false
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun connectDevice(device: Device) {
        bleGatt = device.device?.connectGatt(context, false, object: BluetoothGattCallback() {
        })

        _uiState.update { state ->
            state.copy(
                selectedDevice = device,
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnectDevice() {
        if (bleGatt == null) {
            Log.w(TAG, "already disconnected")
            return
        }
        bleGatt!!.disconnect()
        bleGatt = null
    }
}

data class UiState(
    val deviceList: List<Device> = emptyList(),
    val scanning: Boolean = false,
    val selectedDevice: Device? = null,
)

data class Device(
    val name: String = "",
    val address: String = "",
    val ssid: Int = 0,
    val device: BluetoothDevice? = null,
    val scanRecord: ScanRecord? = null,
)
