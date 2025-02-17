/*
 *     This file is a part of DroidPad (https://www.github.com/umer0586/DroidPad)
 *     Copyright (C) 2025 Umer Farooq (umerfarooq2383@gmail.com)
 *
 *     DroidPad is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     DroidPad is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with DroidPad. If not, see <https://www.gnu.org/licenses/>.
 *
 */


package com.github.umer0586.droidpad.ui.screens.qrscannerscreen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.data.ExternalData
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun QRCodeScannerScreen(
    viewModel: QRScannerScreenViewModel,
    onBackPress: (() -> Unit)? = null,
    onExternalDataAvailable: ((ExternalData) -> Unit)? = null
) {
    viewModel.onExternalDataAvailable {
        onExternalDataAvailable?.invoke(it)
    }

    val uiState by viewModel.uiState.collectAsState()

    QRCodeScannerScreenContent(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)

            when (event) {
                is QRScannerScreenEvent.OnBackPress -> {
                    onBackPress?.invoke()
                }

                else -> {}
            }

        }
    )


}
// Because of its dependency on PermissionState not all of its content could be viewed in a Preview Mode
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRCodeScannerScreenContent(
    uiState: QRScannerScreenState,
    onEvent: (QRScannerScreenEvent) -> Unit
) {

    BackHandler {
        onEvent(QRScannerScreenEvent.OnBackPress)
    }

    val cameraPermissionState =
        if (!LocalInspectionMode.current)
            rememberPermissionState(Manifest.permission.CAMERA)
        else
            null

    if(!LocalInspectionMode.current){
        LaunchedEffect(Unit) {
            cameraPermissionState?.launchPermissionRequest()
        }
    }


    // Launcher for the QR code scanner
    val scanLauncher =
        rememberLauncherForActivityResult(contract = ScanContract()) { result: ScanIntentResult ->
            result.contents?.also { data ->
                onEvent(QRScannerScreenEvent.OnQrCodeScanned(data))
            } ?: onEvent(QRScannerScreenEvent.OnBackPress)

        }

    // Skip if being previewed in Preview mode
    if (!LocalInspectionMode.current) {
        LaunchedEffect(key1 = cameraPermissionState?.status) {
            if (cameraPermissionState?.status == PermissionStatus.Granted) {

                scanLauncher.launch(
                    ScanOptions().apply {
                        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        setCameraId(0)
                        setBeepEnabled(false)
                    })


            } else if (cameraPermissionState?.status?.shouldShowRationale == true) {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            if( cameraPermissionState?.status != PermissionStatus.Granted && !LocalInspectionMode.current){
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val context = LocalContext.current
                    Text("Camera permission required")
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Grant")
                    }
                }
            }

            else if (uiState.decoding) {
                LinearProgressIndicator()
            }

            else if (uiState.decodingFailed) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Failed to Decode !")
                    Button(
                        onClick = {
                            scanLauncher.launch(
                                ScanOptions().apply {
                                    setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                                    setCameraId(0)
                                    setBeepEnabled(false)
                                })
                        }
                    ) { Text("Re-scan") }
                }
            } else if (uiState.decodingSuccess) {
                Text("Decoded Successfully")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun QRCodeScannerContentPreview(modifier: Modifier = Modifier) {
    DroidPadTheme {
        QRCodeScannerScreenContent(
            uiState = QRScannerScreenState(
                decodingSuccess = false,
                decodingFailed = true,
                decoding = false,
            ),
            onEvent = {}
        )
    }
}
