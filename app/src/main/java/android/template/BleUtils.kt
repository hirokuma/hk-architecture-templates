package android.template

import android.bluetooth.BluetoothManager
import android.content.Context

object BleUtils {
    fun isBluetoothEnabled(context: Context): Boolean {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter
        return bluetoothAdapter.isEnabled
    }
}