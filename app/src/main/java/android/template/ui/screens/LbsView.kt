package android.template.ui.screens

import android.template.ble.BleServiceBase
import android.template.ble.LbsService
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

@Composable
fun LbsView(
    uiState: UiState,
    services: Map<UUID, BleServiceBase>
) {
    val service = services[LbsService.SERVICE_UUID]!! as LbsService

    Column(modifier = Modifier) {
        Text(
            text = "LED",
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primaryContainer)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Button(
                onClick = { service.setLed(true) }
            ) {
                Text("ON")
            }
            Button(
                onClick = { service.setLed(false) }
            ) {
                Text("OFF")
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Button",
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primaryContainer)
        )
        Text(
            text = "D",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}
