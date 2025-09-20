/*
 *     This file is a part of DroidPad (https://www.github.com/UmerCodez/DroidPad)
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


package com.github.umer0586.droidpad.ui.screens.qrgeneratorscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.data.util.ImageShareUtil
import com.github.umer0586.droidpad.data.util.QRCodeGenerator
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme


@Composable
fun QrCodeGeneratorScreen(
    viewModel: QrCodeScreenViewModel = hiltViewModel(),
    controlPad: ControlPad,
    onBackPress: (() -> Unit)? = null

) {
    val uiState by viewModel.uiState.collectAsState()

    QrCodeScreenContent(
        controlPad = controlPad,
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)

            if(event is QRCodeScreenEvent.OnBackPress){
                onBackPress?.invoke()
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QrCodeScreenContent(
    controlPad: ControlPad,
    uiState: QRCodeScreenState,
    onEvent: (QRCodeScreenEvent) -> Unit
) {

    val context = LocalContext.current

    BackHandler {
        onEvent(QRCodeScreenEvent.OnBackPress)
    }

    LaunchedEffect(Unit) {
        onEvent(QRCodeScreenEvent.OnGenerateQRCode(controlPad))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("QR Code")
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .clickable { onEvent(QRCodeScreenEvent.OnBackPress) },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "BackIcon"
                    )
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .clickable(enabled = uiState.qrCodeReady) {
                                uiState.qrCodeImage?.also { image ->
                                    ImageShareUtil.shareBitmap(
                                        context,
                                        image
                                    )
                                }
                            },
                        imageVector = Icons.Filled.Share,
                        contentDescription = "ShareIcon"
                    )
                }
            )
        },

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ){
            if(uiState.creatingQrCode){
                LinearProgressIndicator()
            }

            else if(uiState.qrCodeReady && uiState.qrCodeImage != null){
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = uiState.qrCodeImage.asImageBitmap(),
                    contentDescription = null
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun QRCodeScreenContentPreview(modifier: Modifier = Modifier) {

    val controlPad = ControlPad(
        name = "abc",
        orientation = Orientation.PORTRAIT,
    )
    DroidPadTheme {
        QrCodeScreenContent(
            controlPad = controlPad,
            uiState = QRCodeScreenState(
                creatingQrCode = false,
                qrCodeReady = true,
                qrCodeImage = QRCodeGenerator.createQRCode("Hello World", 1000)
            ),
            onEvent = {}
        )
    }
}



