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


package com.github.umer0586.droidpad.ui.screens.jsonimporterscreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.R
import com.github.umer0586.droidpad.data.ExternalData
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
fun JsonImporterScreen(
    viewModel: JsonImporterScreenViewModel,
    onExternalDataAvailable: ((ExternalData) -> Unit)? = null,
    onBackPress: (() -> Unit)? = null
) {

    viewModel.onExternalDataAvailable {
        onExternalDataAvailable?.invoke(it)
    }

    val uiState by viewModel.uiState.collectAsState()

    JsonImporterScreenContent(
        state = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            if (event is JsonImporterScreenEvent.OnBackPress)
                onBackPress?.invoke()
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JsonImporterScreenContent(
    state: JsonImporterScreenState,
    onEvent: (JsonImporterScreenEvent) -> Unit,
) {

    BackHandler {
        onEvent(JsonImporterScreenEvent.OnBackPress)
    }

    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current


    // File picker launcher
    val pickJsonLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                coroutineScope.launch {
                    val jsonString = readJsonFile(context, it)
                    onEvent(JsonImporterScreenEvent.OnJsonImportedFromFile(jsonString))
                }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import Json") },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .clickable { onEvent(JsonImporterScreenEvent.OnBackPress) },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
            ,
            verticalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Icon(
                modifier = Modifier
                    .size(150.dp),
                painter = painterResource(R.drawable.ic_json),
                contentDescription = "JsonIcon"
            )


            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.7f)
            )

            Button(
                onClick = {
                    try {
                        pickJsonLauncher.launch("application/json")
                    } catch (e: ActivityNotFoundException) {

                        coroutineScope.launch {
                            snackBarHostState.showSnackbar("No file picker app found for Json files")
                        }
                    }
                },
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "Import From File",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Button(
                onClick = { onEvent(JsonImporterScreenEvent.OnImportFromLinkClick) },
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "Import From Link",
                    style = MaterialTheme.typography.titleMedium
                )
            }


        }

    }

    if (state.showDownloaderSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(JsonImporterScreenEvent.OnDownloaderSheetDismissRequest)
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var link by remember { mutableStateOf("https://goto.now/mk1YT") }
                OutlinedTextField(
                    value = link,
                    singleLine = true,
                    onValueChange = { link = it },
                    shape = RoundedCornerShape(50),
                    label = { Text("Url") },
                    isError = !link.isValidHttpUrl(),
                    maxLines = 1
                )

                if(state.downloading)
                    LinearProgressIndicator()

                Button(
                    onClick = {
                        onEvent(JsonImporterScreenEvent.OnDownloadClick(link))
                    },
                    enabled = if(!state.downloading) link.isValidHttpUrl() else false
                ) {
                    Text("Download")
                }
            }
        }
    }

    if(state.showErrorSheet){
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(JsonImporterScreenEvent.OnErrorSheetDismissRequest)
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Error")
                HorizontalDivider()
                Text(
                    text = state.error,
                    textAlign = TextAlign.Center
                )

            }
        }

    }


}

@Preview(showBackground = true)
@Composable
private fun JsonImporterScreenContentPreview(modifier: Modifier = Modifier) {

    DroidPadTheme {
        JsonImporterScreenContent(
            state = JsonImporterScreenState(
                downloading = false,
                error = "Error",
            ),
            onEvent = {}
        )
    }
}

// Function to read JSON file from URI
private suspend fun readJsonFile(context: Context, uri: Uri): String {

    val jsonContent = withContext(Dispatchers.IO) {

        return@withContext context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().use { it.readText() }
        } ?: throw IOException("Failed to read JSON file")
    }


    return jsonContent
}

private fun String.isValidHttpUrl(): Boolean {
    return Regex("^(https?://)([\\w.-]+)(:\\d+)?(/.*)?\$").matches(this)
}
