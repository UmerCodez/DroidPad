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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.umer0586.droidpad.data.ExternalData
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRCodeScannerScreen(
    viewModel: QRScannerScreenViewModel = hiltViewModel(),
    onBackPress: (() -> Unit)? = null,
    onExternalDataAvailable: ((ExternalData) -> Unit)? = null
) {
    viewModel.onExternalDataAvailable {
        onExternalDataAvailable?.invoke(it)
    }

    val uiState by viewModel.uiState.collectAsState()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    //If shouldShowRationale == true,
    //  1. It means that the app should show an explanation before requesting the permission again. This usually happens when the user has denied the permission once but has not selected "Don't ask again".
    //If shouldShowRationale == false, it means that:
    //  1. The permission has never been requested before.
    //  2. The user granted the permission.
    //  3. The user denied the permission and selected "Don't ask again" (in this case, further requests will not show a system dialog).

    LaunchedEffect(cameraPermissionState.status) {
        if (!cameraPermissionState.status.isGranted) {
            if (cameraPermissionState.status.shouldShowRationale) {
                // Show rationale for permission
                viewModel.onEvent(QRScannerScreenEvent.OnShouldShowRationale)
            } else {
                // Check if permission is permanently denied
                if (!cameraPermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale) {
                    // Permission is permanently denied, guide user to app settings
                    viewModel.onEvent(QRScannerScreenEvent.OnPermissionPermanentlyDenied)
                } else {
                    // Request permission
                    cameraPermissionState.launchPermissionRequest()
                }
            }
        } else { // cameraPermissionState.status.isGranted
            // Permission is granted
            viewModel.onEvent(QRScannerScreenEvent.OnCameraPermissionGranted)
        }

        cameraPermissionState.launchPermissionRequest()
    }

    QRCodeScannerScreenContent(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)

            when (event) {
                is QRScannerScreenEvent.OnBackPress -> {
                    onBackPress?.invoke()
                }

                is QRScannerScreenEvent.OnGrantPermissionClick -> {
                    cameraPermissionState.launchPermissionRequest()
                }

                else -> {}
            }

        }
    )


}

@Composable
fun QRCodeScannerScreenContent(
    uiState: QRScannerScreenState,
    onEvent: (QRScannerScreenEvent) -> Unit
) {

    BackHandler {
        onEvent(QRScannerScreenEvent.OnBackPress)
    }
    // Launcher for the QR code scanner
    val scanLauncher =
        rememberLauncherForActivityResult(contract = ScanContract()) { result: ScanIntentResult ->
            result.contents?.also { data ->
                onEvent(QRScannerScreenEvent.OnQrCodeScanned(data))
            } ?: onEvent(QRScannerScreenEvent.OnBackPress)

        }

    if(uiState.cameraPermissionGranted){
        LaunchedEffect(Unit) {
            scanLauncher.launch(
                ScanOptions().apply {
                    setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    setCameraId(0)
                    setBeepEnabled(false)
                })
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            if(uiState.cameraPermissionPermanentlyDenied){
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val context = LocalContext.current
                    Text(
                        "You have denied camera permission permanently. Please grant it from settings.",
                        textAlign = TextAlign.Center
                    )
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
                        Text("Open Settings")
                    }
                }
            }
            else if(uiState.shouldShowRationale){
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )  {
                    Text(
                        "Camera permission is required to scan QR codes",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { onEvent(QRScannerScreenEvent.OnGrantPermissionClick) }
                    ) {
                        Text("Grant Permission")
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
                    Spacer(modifier = Modifier.height(20.dp))
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
                cameraPermissionPermanentlyDenied = true,
                decodingSuccess = false,
                decodingFailed = true,
                decoding = false,
            ),
            onEvent = {}
        )
    }
}
