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

package android.template.ui

import android.template.data.ble.LbsService
import android.template.data.ble.LpsService
import android.template.ui.screens.BleViewModel
import android.template.ui.screens.CheckPermissionsScreen
import android.template.ui.screens.ConnectedScreen
import android.template.ui.screens.ScanScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class NavRoute {
    CheckPermissions,
    Scan,
    Connected,
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    // TODO Add BLE service classes
    val lbsService = LbsService()
    val lpsService = LpsService()
    val services = mapOf(
        lbsService.serviceUuid to lbsService,
        lpsService.serviceUuid to lpsService,
    )

    val viewModel = BleViewModel(LocalContext.current.applicationContext, services)

    NavHost(navController = navController, startDestination = NavRoute.CheckPermissions.name) {
        composable(NavRoute.CheckPermissions.name) {
            CheckPermissionsScreen(
                onCheckPassed = {
                    navController.navigate(NavRoute.Scan.name) {
                        popUpTo(NavRoute.CheckPermissions.name) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoute.Scan.name) {
            ScanScreen(
                viewModel,
                onItemClicked = {
                    navController.navigate(NavRoute.Connected.name)
                },
                modifier = Modifier.padding(16.dp))
        }
        composable(NavRoute.Connected.name) {
            ConnectedScreen(
                viewModel,
                services,
                goToBack = {
                    navController.popBackStack()
                },
                modifier = Modifier.padding(16.dp))
        }
    }
}
