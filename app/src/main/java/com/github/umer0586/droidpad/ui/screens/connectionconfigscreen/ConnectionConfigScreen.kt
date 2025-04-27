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

package com.github.umer0586.droidpad.ui.screens.connectionconfigscreen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chargemap.compose.numberpicker.NumberPicker
import com.github.umer0586.droidpad.data.connectionconfig.RemoteBluetoothDevice
import com.github.umer0586.droidpad.data.connectionconfig.UUID_SSP
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import com.github.umer0586.droidpad.ui.components.EnumDropdown
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import kotlinx.coroutines.launch


@Composable
fun ConnectionConfigScreen(
    controlPadId: Long,
    viewModel: ConnectionConfigScreenViewModel = hiltViewModel(),
    onConfigSaved: (() -> Unit)? = null,
    onBackPress: (() -> Unit)? = null
) {
    // Always gets call when user navigates to this screen
    LaunchedEffect(Unit) {
        viewModel.loadConnectionConfigFor(controlPadId)
    }

    viewModel.onConfigSaved {
        onConfigSaved?.invoke()
    }

    val uiState by viewModel.uiState.collectAsState()

    ConnectionConfigScreenContent(
        controlPadId = controlPadId,
        uiState = uiState,
        onUiEvent = { event ->
            viewModel.onEvent(event)
            if (event is ConnectionConfigScreenEvent.OnBackPress)
                onBackPress?.invoke()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionConfigScreenContent(
    controlPadId: Long,
    uiState: ConnectionConfigScreenState,
    onUiEvent: (ConnectionConfigScreenEvent) -> Unit
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Connection Config") },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .clickable { onUiEvent(ConnectionConfigScreenEvent.OnBackPress) },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            )
        }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val itemWidth = 300.dp
            val itemPadding = 10.dp

            EnumDropdown<ConnectionType>(
                selectedValue = uiState.connectionType,
                label = "Connection Type",
                onValueSelected = {onUiEvent(ConnectionConfigScreenEvent.OnConnectionTypeChange(it))}
            )

            if(uiState.connectionType == ConnectionType.TCP ||
                uiState.connectionType == ConnectionType.UDP ||
                uiState.connectionType == ConnectionType.WEBSOCKET ||
                uiState.connectionType == ConnectionType.MQTT_V5 ||
                uiState.connectionType == ConnectionType.MQTT_V3
            ){

                OutlinedTextField(
                    value = uiState.host,
                    singleLine = true,
                    onValueChange = {onUiEvent(ConnectionConfigScreenEvent.OnHostChange(it))},
                    shape = RoundedCornerShape(50),
                    label = { Text("Host") },
                    isError = uiState.host.isEmpty(),
                    maxLines = 1
                )
                OutlinedTextField(
                    value = uiState.port.toString(),
                    isError = !uiState.isPortNoValid,
                    singleLine = true,
                    onValueChange = {
                        onUiEvent(ConnectionConfigScreenEvent.OnPortChange(it))
                    },
                    shape = RoundedCornerShape(50),
                    label = { Text("Port") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if(uiState.connectionType != ConnectionType.UDP) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Connection Timeout seconds")
                        NumberPicker(
                            value = uiState.connectionTimeout,
                            onValueChange = {
                                onUiEvent(
                                    ConnectionConfigScreenEvent.OnConnectionTimeoutChange(
                                        it
                                    )
                                )
                            },
                            dividersColor = MaterialTheme.colorScheme.primary,
                            range = 1..60,
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }

                }


            }

            if(uiState.connectionType == ConnectionType.MQTT_V5 || uiState.connectionType == ConnectionType.MQTT_V3){

                OutlinedTextField(
                    value = uiState.clientId,
                    singleLine = true,
                    onValueChange = {onUiEvent(ConnectionConfigScreenEvent.OnClientIdChange(it))},
                    shape = RoundedCornerShape(50),
                    label = { Text("Client ID") },
                    isError = uiState.clientId.isEmpty()
                )

                OutlinedTextField(
                    value = uiState.topic,
                    singleLine = true,
                    onValueChange = {onUiEvent(ConnectionConfigScreenEvent.OnTopicChange(it))},
                    shape = RoundedCornerShape(50),
                    label = { Text("Topic") },
                    isError = uiState.topic.isEmpty() || uiState.topic.contains(Regex("\\s+"))
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Qos")
                    SingleChoiceSegmentedButtonRow {
                        (0..2).forEachIndexed{ index,qos ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                                selected = qos == uiState.qos,
                                onClick = {
                                    onUiEvent(
                                        ConnectionConfigScreenEvent.OnQosChange(qos)
                                    )
                                },
                                label = {Text(qos.toString())}
                            )
                        }
                    }
                }

                // Dont't use ListItem() Composable
                // We want this item to be of same with as above OutLineTextFields
                OutlinedTextField(
                    value = "SSL",
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = false,
                    onValueChange = {},
                    trailingIcon = {
                        Switch(
                            checked = uiState.useSSL,
                            onCheckedChange = { onUiEvent(
                                ConnectionConfigScreenEvent.OnUseSSLChange(
                                    it
                                )
                            ) }
                        )
                    }
                )

                OutlinedTextField(
                    value = "Websocket",
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = false,
                    onValueChange = {},
                    trailingIcon = {
                        Switch(
                            checked = uiState.useWebsocket,
                            onCheckedChange = { onUiEvent(
                                ConnectionConfigScreenEvent.OnUseWebsocketChange(
                                    it
                                )
                            ) }
                        )
                    }
                )

                OutlinedTextField(
                    value = "User Credentials",
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = false,
                    onValueChange = {},
                    trailingIcon = {
                        Switch(
                            checked = uiState.useCredentials,
                            onCheckedChange = { onUiEvent(
                                ConnectionConfigScreenEvent.OnUseCredentialChange(
                                    it
                                )
                            ) }
                        )
                    }
                )


                AnimatedVisibility(uiState.useCredentials) {

                    OutlinedTextField(
                        value = uiState.username,
                        singleLine = true,
                        onValueChange = {
                            onUiEvent(ConnectionConfigScreenEvent.OnUsernameChange(it))
                        },
                        shape = RoundedCornerShape(50),
                        label = { Text("User Name") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "UserName"
                            )
                        }
                    )

                }

                AnimatedVisibility(uiState.useCredentials) {

                    OutlinedTextField(
                        value = uiState.password,
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = {
                            onUiEvent(ConnectionConfigScreenEvent.OnPasswordChange(it))
                        },
                        shape = RoundedCornerShape(50),
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "UserName"
                            )
                        }
                    )

                }


            }

            if(uiState.connectionType == ConnectionType.BLUETOOTH){


                OutlinedTextField(
                    modifier = Modifier
                        .width(itemWidth)
                        .padding(horizontal = itemPadding),
                    value = uiState.bluetoothServiceUUID,
                    singleLine = true,
                    supportingText = {
                        if(uiState.bluetoothServiceUUID.equals(UUID_SSP, ignoreCase = true))
                            Text("Serial Port Profile")
                    },
                    onValueChange = {
                        onUiEvent(
                            ConnectionConfigScreenEvent.OnBluetoothUUIDChange(
                                it
                            )
                        )
                    },
                    shape = RoundedCornerShape(50),
                    label = { Text("Service UUID") },
                    isError = uiState.bluetoothServiceUUID.isEmpty()
                )

                var showPairedDevices by remember { mutableStateOf(false) }

                ListItem(
                    modifier = Modifier
                        .width(itemWidth)
                        .padding(horizontal = itemPadding),
                    headlineContent = { Text(uiState.selectedBluetoothDevice?.name ?: "No Device Selected") },
                    supportingContent = { uiState.selectedBluetoothDevice?.address?.also { Text(it) } },
                    trailingContent = {
                        IconButton(
                            onClick = {


                                if(!uiState.isBluetoothEnable){
                                    scope.launch {
                                        snackBarHostState.showSnackbar("Please Enable Bluetooth")
                                    }
                                }else if(uiState.pairedBluetoothDevices.isNotEmpty()){
                                    showPairedDevices = true
                                } else { // when bluetooth is enabled but there are no paired devices
                                    scope.launch {
                                        snackBarHostState.showSnackbar("No paired devices found")
                                    }
                                }

                                onUiEvent(ConnectionConfigScreenEvent.OnSelectDeviceClick)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Select Device"
                            )
                        }
                    }
                )

                if(showPairedDevices){
                    ModalBottomSheet(
                        onDismissRequest = { showPairedDevices = false },
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {

                            items(uiState.pairedBluetoothDevices.size) { index ->
                                val bluetoothDevice = uiState.pairedBluetoothDevices[index]
                                ListItem(
                                    modifier = Modifier.clickable {
                                        onUiEvent(ConnectionConfigScreenEvent.OnBluetoothDeviceSelected(bluetoothDevice))
                                        showPairedDevices = false
                                    },
                                    headlineContent = { Text(bluetoothDevice.name) },
                                    supportingContent = { Text(bluetoothDevice.address) },
                                )
                            }
                        }
                    }
                }
            }


            TextButton(
                modifier = Modifier.fillMaxWidth(0.4f),
                contentPadding = PaddingValues(20.dp),
                onClick = { onUiEvent(ConnectionConfigScreenEvent.OnSaveClick(controlPadId)) },
                enabled = !uiState.hasInputError,
                colors = ButtonDefaults.textButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) { Text("Save") }
        }

    }



}


@Preview(showBackground = true)
@Composable
private fun ConnectionConfigScreenContentPreview()  {

    val pairedDevices = listOf(
        RemoteBluetoothDevice("Device 1", "1234"),
        RemoteBluetoothDevice("Device 2", "5678"),
        RemoteBluetoothDevice("Device 2", "5678")
    )

    var uiState by remember {
        mutableStateOf(
            ConnectionConfigScreenState(
                connectionType = ConnectionType.BLUETOOTH,
                bluetoothServiceUUID = "00001101-0000-1000-8000-00805F9B34FB",
                selectedBluetoothDevice = null,
            )
        )
    }

    DroidPadTheme {
        ConnectionConfigScreenContent(
            controlPadId = 100,
            uiState = uiState,
            onUiEvent = { event->
                when (event) {
                    is ConnectionConfigScreenEvent.OnPortChange -> {

                    }
                    is ConnectionConfigScreenEvent.OnConnectionTimeoutChange -> {
                        uiState = uiState.copy(connectionTimeout = event.connectionTimeout)
                    }

                    is ConnectionConfigScreenEvent.OnQosChange -> {
                        uiState = uiState.copy(qos = event.qos)
                    }

                    is ConnectionConfigScreenEvent.OnUseSSLChange -> {
                        uiState = uiState.copy(useSSL = event.sslEnabled)
                    }
                    is ConnectionConfigScreenEvent.OnSelectDeviceClick -> {
                        uiState = uiState.copy(pairedBluetoothDevices = listOf())
                    }
                    is ConnectionConfigScreenEvent.OnBluetoothDeviceSelected -> {
                        uiState = uiState.copy(selectedBluetoothDevice = event.remoteBluetoothDevice)
                    }

                    else -> {}
                }
            }
        )
    }
}