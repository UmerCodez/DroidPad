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


package com.github.umer0586.droidpad.ui.screens.controlpadsscreen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.umer0586.droidpad.R
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


@Composable
fun ControlPadsScreen(
    viewModel : ControlPadsScreenViewModel = hiltViewModel(),
    onCreateClick: (() -> Unit)? = null,
    onBuildClick: ((ControlPad) -> Unit)? = null,
    onSettingClick: ((ControlPad) -> Unit)? = null,
    onPlayClick: ((ControlPad) -> Unit)? = null,
    onExitClick: (() -> Unit)? = null,
    onAboutClick: (() -> Unit)? = null,
    onQRGenerateClick: ((ControlPad) -> Unit)? = null,
    onQrScannerClick: (() -> Unit)? = null,
    onImportJsonClick: (() -> Unit)? = null,
    onPreferenceClick:(() -> Unit)? = null,
    onAttachSensorsClick: ((ControlPad) -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadConnectionTypes()
    }

    val context = LocalContext.current
    viewModel.onExportableJsonReady { externalData ->
        shareJsonStringAsFile(
            context = context,
            jsonString = externalData.toJson(),
            name = externalData.controlPad.name
        )
    }

    ControlPadsScreenContent(
        appVersion = getAppVersion(context),
        uiState = uiState,
        onUiEvent = { event ->
            viewModel.onEvent(event)

            when(event){
                is ControlPadsScreenEvent.OnCreateClick -> onCreateClick?.invoke()
                is ControlPadsScreenEvent.OnBuildClick -> onBuildClick?.invoke(event.controlPad)
                is ControlPadsScreenEvent.OnSettingClick -> onSettingClick?.invoke(event.controlPad)
                is ControlPadsScreenEvent.OnPlayClick -> onPlayClick?.invoke(event.controlPad)
                is ControlPadsScreenEvent.OnExitClick -> onExitClick?.invoke()
                is ControlPadsScreenEvent.OnAboutClick -> onAboutClick?.invoke()
                is ControlPadsScreenEvent.OnShareClick -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    if(intent.resolveActivity(context.packageManager) != null){
                        intent.data = Uri.parse("https://www.github.com/UmerCodez/DroidPad")
                        context.startActivity(Intent.createChooser(intent,"Select Browser"))
                    } else {
                        Toast.makeText(context,"No browser found", Toast.LENGTH_SHORT).show()
                    }
                }
                is ControlPadsScreenEvent.OnQrCodeClick -> onQRGenerateClick?.invoke(event.controlPad)
                is ControlPadsScreenEvent.OnQRScannerClick -> onQrScannerClick?.invoke()
                is ControlPadsScreenEvent.OnImportJsonClick -> onImportJsonClick?.invoke()
                is ControlPadsScreenEvent.OnPreferenceClick -> onPreferenceClick?.invoke()
                is ControlPadsScreenEvent.OnAttachSensorsClick -> onAttachSensorsClick?.invoke(event.controlPad)
                else -> {}
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlPadsScreenContent(
    appVersion: String = "X.Y.Z",
    uiState: ControlPadsScreenState,
    onUiEvent: (ControlPadsScreenEvent) -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    Spacer(Modifier.height(20.dp))

                    Icon(
                        modifier = Modifier.clickable { 
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Menu"
                    )

                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ){
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Droid Pad",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "v$appVersion",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    HorizontalDivider()

                    NavigationDrawerItem(
                        label = { Text("Import Json") },
                        icon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.ic_json),
                                contentDescription = "JsonIcon"
                            )
                        },
                        selected = false,
                        onClick = { onUiEvent(ControlPadsScreenEvent.OnImportJsonClick) }
                    )

                    NavigationDrawerItem(
                        label = { Text("Preferences") },
                        icon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "PreferencesIcon"
                            )
                        },
                        selected = false,
                        onClick = { onUiEvent(ControlPadsScreenEvent.OnPreferenceClick) }
                    )

                    NavigationDrawerItem(
                        label = { Text("About") },
                        icon = { Icon(Icons.Filled.Info , null) },
                        selected = false,
                        onClick = { onUiEvent(ControlPadsScreenEvent.OnAboutClick) }
                    )
                    NavigationDrawerItem(
                        label = { Text("Share") },
                        icon = { Icon(Icons.Filled.Share , null) },
                        selected = false,
                        onClick = { onUiEvent(ControlPadsScreenEvent.OnShareClick) }
                    )
                    NavigationDrawerItem(
                        label = { Text("Exit") },
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                        selected = false,
                        onClick = { onUiEvent(ControlPadsScreenEvent.OnExitClick) }
                    )

                }
            }
        }

    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Control Pads") },
                    navigationIcon = {
                        Icon(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp)
                                .clickable {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                },
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "MenuIcon"
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {onUiEvent(ControlPadsScreenEvent.OnQRScannerClick)}
                        ){
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.ic_qr_scanner),
                                contentDescription = "ScanIcon"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        onUiEvent(ControlPadsScreenEvent.OnCreateClick)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "CreateIcon"
                    )
                }

            }
        ) { innerPadding ->


            if (uiState.controlPads.size == 0) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No control pads found", style = MaterialTheme.typography.titleLarge)
                }

            }

            var showNameEditorSheet by remember { mutableStateOf(false) }

            if(showNameEditorSheet && uiState.itemToBeEdited != null){
                ModalBottomSheet(
                    onDismissRequest = { showNameEditorSheet = false }
                ) {
                    ControlPadNameEditor(
                        controlPad = uiState.itemToBeEdited,
                        onUpdateClick = { updatedControlPad ->
                            onUiEvent(ControlPadsScreenEvent.OnNameUpdate(updatedControlPad))
                            showNameEditorSheet = false
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                items(uiState.controlPads.toList()) { controlPad ->

                    var showDeletionAlert by remember { mutableStateOf(false) }

                    if (showDeletionAlert) {
                        AlertDialog(
                            onDismissRequest = { showDeletionAlert = false },
                            title = { Text(text = "Delete Control Pad") },
                            text = { Text(text = "Delete Item") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDeletionAlert = false
                                        onUiEvent(ControlPadsScreenEvent.OnDeleteClick(controlPad))
                                    }
                                ) { Text("Yes") }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showDeletionAlert = false
                                    }
                                ) { Text("No") }
                            }
                        )
                    }


                    ItemCard(
                        modifier = Modifier.padding(10.dp),
                        controlPad = controlPad,
                        connectionType = uiState.controlPadConnectionTypeMap[controlPad.id] ?: ConnectionType.WEBSOCKET,
                        onEditClick = {
                            onUiEvent(ControlPadsScreenEvent.OnEditClick(it))
                            showNameEditorSheet = true
                        },
                        onDeleteClick = {
                            showDeletionAlert = true
                        },
                        onPlayClick = {
                            onUiEvent(ControlPadsScreenEvent.OnPlayClick(it))
                        },
                        onSettingClick = {
                            onUiEvent(ControlPadsScreenEvent.OnSettingClick(it))
                        },
                        onBuildClick = {
                            onUiEvent(ControlPadsScreenEvent.OnBuildClick(it))
                        },
                        onDuplicateClick = {
                            onUiEvent(ControlPadsScreenEvent.OnDuplicateClick(it))
                        },
                        onQRCodeClick = {
                            onUiEvent(ControlPadsScreenEvent.OnQrCodeClick(it))
                        },
                        onExportJsonClick = {
                            onUiEvent(ControlPadsScreenEvent.OnExportJsonClick(it))
                        },
                        onAttachSensorsClick = {
                            onUiEvent(ControlPadsScreenEvent.OnAttachSensorsClick(it))
                        },
                        onLoggingChange = { controlPad, logging ->
                            onUiEvent(ControlPadsScreenEvent.OnLoggingChange(controlPad.copy(logging = logging)))
                        }

                    )
                }
            }
        }
    }

}

@Composable
private fun ItemCard(
    modifier: Modifier = Modifier,
    controlPad: ControlPad,
    connectionType: ConnectionType = ConnectionType.WEBSOCKET,
    onEditClick: ((ControlPad) -> Unit)? = null,
    onDeleteClick: ((ControlPad) -> Unit)? = null,
    onPlayClick: ((ControlPad) -> Unit)? = null,
    onSettingClick: ((ControlPad) -> Unit)? = null,
    onBuildClick: ((ControlPad) -> Unit)? = null,
    onDuplicateClick: ((ControlPad) -> Unit)? = null,
    onQRCodeClick: ((ControlPad) -> Unit)? = null,
    onExportJsonClick: ((ControlPad) -> Unit)? = null,
    onAttachSensorsClick: ((ControlPad) -> Unit)? = null,
    onLoggingChange: ((ControlPad, Boolean) -> Unit)? = null,

    ){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.7f),
                text = controlPad.name,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                maxLines = 1

            )

            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = {
                    onEditClick?.invoke(controlPad)
                },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "EditIcon"
                    )
                }
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = connectionType.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                IconButton(
                    onClick = {
                        onSettingClick?.invoke(controlPad)
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "SettingsIcon"
                        )
                    }
                )
            }

            Box(
                modifier = Modifier.align(Alignment.BottomStart)
            ){

                var expanded by remember { mutableStateOf(false) }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Export Json") },
                        onClick = {
                            onExportJsonClick?.invoke(controlPad)
                            expanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Attach Sensors") },
                        onClick = {
                            onAttachSensorsClick?.invoke(controlPad)
                            expanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {Text("Logging")},
                        onClick = {
                            expanded = false
                        },
                        trailingIcon = {
                            Switch(
                                modifier = Modifier.graphicsLayer{
                                    scaleX = 0.6f
                                    scaleY = 0.6f
                                },
                                checked = controlPad.logging,
                                onCheckedChange = {
                                    onLoggingChange?.invoke(controlPad, it)

                                }
                            )
                        }
                    )

                }
            }

            Row(modifier = Modifier.align(Alignment.BottomCenter)){

                IconButton(
                    onClick = {
                        onPlayClick?.invoke(controlPad)
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "PlayIcon"
                        )
                    }
                )


                IconButton(
                    onClick = {
                        onBuildClick?.invoke(controlPad)
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Build,
                            contentDescription = "BuildIcon"
                        )
                    }
                )

                IconButton(
                    onClick = {
                        onDeleteClick?.invoke(controlPad)
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "DeleteIcon"
                        )
                    }
                )

                IconButton(
                    onClick = {
                        onDuplicateClick?.invoke(controlPad)
                    },
                    content = {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(R.drawable.ic_copy),
                            contentDescription = "DuplicateIcon"
                        )
                    }
                )

                IconButton(
                    onClick = {
                        onQRCodeClick?.invoke(controlPad)
                    },
                    content = {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(R.drawable.ic_qrcode),
                            contentDescription = "QRIcon"
                        )
                    }
                )



            }

        }
    }
}

@Composable
private fun ControlPadNameEditor(
    modifier: Modifier = Modifier,
    controlPad: ControlPad,
    onUpdateClick: ((ControlPad) -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var modifiedControlPad by remember { mutableStateOf(controlPad) }
        OutlinedTextField(
            value = modifiedControlPad.name,
            isError = modifiedControlPad.name.isEmpty(),
            singleLine = true,
            onValueChange = {
                modifiedControlPad = modifiedControlPad.copy(name = it)
            },
            label = { Text("Name") },
            shape = RoundedCornerShape(50)
        )

        TextButton(
            onClick = {
                onUpdateClick?.invoke(modifiedControlPad)
            },
            enabled = modifiedControlPad.name.isNotEmpty(),
            colors = ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            contentPadding = PaddingValues(16.dp)
        ) { Text("Update") }
    }
}
@Preview
@Composable
private fun ControlPadNameEditorPreview(modifier: Modifier = Modifier) {
    DroidPadTheme {
        ControlPadNameEditor(
            controlPad = ControlPad(
                name = "MyControlPad",
                orientation = Orientation.LANDSCAPE
            )
        )
    }
}

@Preview
@Composable
private fun ItemCardPreview(){
    DroidPadTheme {
        ItemCard(
            controlPad = ControlPad(
                name = "MyControlPadgfgfhfg fhgf fgf xxx xxdzx ",
                orientation = Orientation.LANDSCAPE
            )
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun ControlPadsScreenContentPreview() {

    var uiState = remember {
        ControlPadsScreenState(
            controlPads = mutableStateListOf(

                ControlPad(
                    name = "MyControlPad",
                    orientation = Orientation.LANDSCAPE
                ),
                ControlPad(
                    name = "MySecondControlPad",
                    orientation = Orientation.PORTRAIT
                ),
                ControlPad(
                    name = "MyThirdControlPad",
                    orientation = Orientation.LANDSCAPE
                )

            )
        )
    }

    DroidPadTheme {
        ControlPadsScreenContent(
            uiState = uiState,
            onUiEvent = {event->
                when(event){
                    is ControlPadsScreenEvent.OnCreateClick -> {}
                    is ControlPadsScreenEvent.OnDeleteClick -> {
                        uiState.controlPads.remove(event.controlPad)
                    }
                    is ControlPadsScreenEvent.OnNameUpdate -> {}
                    is ControlPadsScreenEvent.OnPlayClick -> {}
                    else -> TODO("Not yet implemented")
                }
            }
        )
    }
}

private fun shareJsonStringAsFile(context: Context, jsonString: String, name: String) {
    val fileName = "$name.json"
    val dir = File(context.applicationContext.cacheDir, "shared_json_files").apply { mkdirs() }
    val cacheFile = File(dir, fileName)

    try {
        FileOutputStream(cacheFile).use { fos ->
            OutputStreamWriter(fos).use { writer ->
                writer.write(jsonString)
            }
        }

        val uri: Uri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", cacheFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share JSON File").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooserIntent)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun getAppVersion(context: Context) : String{
    val versionName = try {
        context.applicationContext.packageManager
            .getPackageInfo(context.packageName, 0).versionName ?: "Unknown"

    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
    return versionName
}
