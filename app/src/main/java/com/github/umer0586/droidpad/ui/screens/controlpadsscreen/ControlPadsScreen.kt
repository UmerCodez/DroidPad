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


package com.github.umer0586.droidpad.ui.screens.controlpadsscreen

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ControlPadsScreen(
    viewModel : ControlPadsScreenViewModel = hiltViewModel(),
    appVersion: String = "X.Y.Z",
    onCreateClick: (() -> Unit)? = null,
    onBuildClick: ((ControlPad) -> Unit)? = null,
    onSettingClick: ((ControlPad) -> Unit)? = null,
    onPlayClick: ((ControlPad) -> Unit)? = null,
    onExitClick: (() -> Unit)? = null,
    onAboutClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadConnectionTypes()
    }

    ControlPadsScreenContent(
        appVersion = appVersion,
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
                is ControlPadsScreenEvent.OnShareClick -> onShareClick?.invoke()
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

            if (uiState.loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                }
            }

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
                    ItemCard(
                        modifier = Modifier.padding(10.dp),
                        controlPad = controlPad,
                        connectionType = uiState.controlPadConnectionTypeMap[controlPad.id] ?: ConnectionType.WEBSOCKET,
                        onEditClick = {
                            onUiEvent(ControlPadsScreenEvent.OnEditClick(it))
                            showNameEditorSheet = true
                        },
                        onDeleteClick = {
                            onUiEvent(ControlPadsScreenEvent.OnDeleteClick(it))
                        },
                        onPlayClick = {
                            onUiEvent(ControlPadsScreenEvent.OnPlayClick(it))
                        },
                        onSettingClick = {
                            onUiEvent(ControlPadsScreenEvent.OnSettingClick(it))
                        },
                        onBuildClick = {
                            onUiEvent(ControlPadsScreenEvent.OnBuildClick(it))
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
    onBuildClick: ((ControlPad) -> Unit)? = null

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

            ),
            loading = false
        )
    }
    
    LaunchedEffect(Unit) {

        uiState = uiState.copy(loading = true)
        delay(2000)
        uiState = uiState.copy(loading = false)
        listOf(
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
        ).forEach {
                uiState.controlPads.add(it)
        }
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