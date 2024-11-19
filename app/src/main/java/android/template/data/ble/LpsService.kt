package android.template.data.ble

import android.util.Log
import java.util.UUID

private const val TAG = "LpsService"

class LpsService(private val bleConnect: BleConnect): BleServiceBase {
    override val serviceUuid = SERVICE_UUID
    override val callback = object : BleServiceCallback(lbsCharacteristicUuids) {}

    fun sendText(text: String) {
        Log.d(TAG, "setText: $text")
        bleConnect.bleGatt?.let { gatt ->
            val service = Utils.getService(gatt, SERVICE_UUID)
            val chars = Utils.getCharacteristic(service, PRINT_CHARACTERISTIC_UUID)
            val data = text.toByteArray()
            Utils.writeCharacteristic(gatt, chars, data)
        }
    }

    fun clearText() {
        Log.d(TAG, "clearText")
        bleConnect.bleGatt?.let { gatt ->
            val service = Utils.getService(gatt, SERVICE_UUID)
            val chars = Utils.getCharacteristic(service, CLEAR_CHARACTERISTIC_UUID)
            val data = byteArrayOf(1)
            Utils.writeCharacteristic(gatt, chars, data)
        }
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("a00c1710-74ff-4bd5-9e86-cf601d80c054")
        private val PRINT_CHARACTERISTIC_UUID = UUID.fromString("a00c1711-74ff-4bd5-9e86-cf601d80c054")
        private val CLEAR_CHARACTERISTIC_UUID = UUID.fromString("a00c1712-74ff-4bd5-9e86-cf601d80c054")

        private val lbsCharacteristicUuids = listOf(
            PRINT_CHARACTERISTIC_UUID,
            CLEAR_CHARACTERISTIC_UUID,
        )
    }
}