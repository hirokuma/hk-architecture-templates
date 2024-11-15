package android.template.ble

import java.util.UUID

interface BleServiceBase {
    val serviceUuid: UUID
    val callback: BleServiceCallback
}