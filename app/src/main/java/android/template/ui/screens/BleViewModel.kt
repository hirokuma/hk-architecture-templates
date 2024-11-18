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

import android.content.Context
import android.template.data.ble.BleConnect
import android.template.data.ble.BleScan
import android.template.data.ble.Device
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "BleViewModel"

class BleViewModel(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val bleScan = BleScan(context)
    private var bleScanJob: Job? = null
    private val bleConn = BleConnect(context)
    val services = bleConn.services

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val disconnectState = bleConn.disconnectState

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

    fun startDeviceScan() {
        if (!_uiState.value.scanning) {
            _uiState.update {
                it.copy(
                    deviceList = emptyList(),
                    scanning = true
                )
            }
            Log.d(TAG, "onClickScan: start searching")
            bleScanJob = viewModelScope.launch(Dispatchers.IO) {
                val scanFlow = bleScan.startScan()
                scanFlow.buffer().collect {
                    addDevice(it)
                }
            }
        } else {
            _uiState.update {
                it.copy(scanning = false)
            }
            Log.d(TAG, "onClickScan: stop searching")
            stopDeviceScan()
        }
    }

    fun stopDeviceScan(): Boolean {
        if (!_uiState.value.scanning) {
            Log.d(TAG, "not scanning")
            return false
        }
        try {
            bleScan.stopScan()
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

    fun connectDevice(device: Device) {
        bleConn.connectDevice(device)
        _uiState.update { state ->
            state.copy(
                selectedDevice = device,
            )
        }
    }

    fun disconnectDevice() {
        bleConn.disconnectDevice()
    }
}

data class UiState(
    val deviceList: List<Device> = emptyList(),
    val scanning: Boolean = false,
    val selectedDevice: Device? = null,
)

