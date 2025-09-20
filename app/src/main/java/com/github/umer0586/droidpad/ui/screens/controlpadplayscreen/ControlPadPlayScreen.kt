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

package com.github.umer0586.droidpad.ui.screens.controlpadplayscreen

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.umer0586.droidpad.R
import com.github.umer0586.droidpad.data.ButtonProperties
import com.github.umer0586.droidpad.data.DpadProperties
import com.github.umer0586.droidpad.data.GaugeProperties
import com.github.umer0586.droidpad.data.JoyStickProperties
import com.github.umer0586.droidpad.data.LEDProperties
import com.github.umer0586.droidpad.data.LabelProperties
import com.github.umer0586.droidpad.data.LogEvent
import com.github.umer0586.droidpad.data.SliderProperties
import com.github.umer0586.droidpad.data.SteeringWheelProperties
import com.github.umer0586.droidpad.data.StepSliderProperties
import com.github.umer0586.droidpad.data.SwitchProperties
import com.github.umer0586.droidpad.data.connection.ConnectionState
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.data.database.entities.offset
import com.github.umer0586.droidpad.ui.bottomBarHeight
import com.github.umer0586.droidpad.ui.components.ControlPadButton
import com.github.umer0586.droidpad.ui.components.ControlPadDpad
import com.github.umer0586.droidpad.ui.components.ControlPadGauge
import com.github.umer0586.droidpad.ui.components.ControlPadJoyStick
import com.github.umer0586.droidpad.ui.components.ControlPadLED
import com.github.umer0586.droidpad.ui.components.ControlPadLabel
import com.github.umer0586.droidpad.ui.components.ControlPadSlider
import com.github.umer0586.droidpad.ui.components.ControlPadSteeringWheel
import com.github.umer0586.droidpad.ui.components.ControlPadStepSlider
import com.github.umer0586.droidpad.ui.components.ControlPadSwitch
import com.github.umer0586.droidpad.ui.components.LEDSTATE
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import com.github.umer0586.droidpad.ui.utils.LockScreenOrientation
import kotlinx.coroutines.launch

@Composable
fun ControlPadPlayScreen(
    controlPad: ControlPad,
    viewModel: ControlPadPlayScreenViewModel = hiltViewModel(),
    onBackPress: (() -> Unit)? = null,
) {

    LockScreenOrientation(
        orientation = when(controlPad.orientation){
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadControlPadItemsFor(controlPad)
    }


    ControlPlayScreenContent(
        controlPad = controlPad,
        uiState = uiState,
        onUiEvent = {event->
            viewModel.onEvent(event)
            
            if(event is ControlPadPlayScreenEvent.OnBackPress)
                onBackPress?.invoke()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlPlayScreenContent(
    controlPad: ControlPad,
    uiState: ControlPadPlayScreenState,
    onUiEvent: (ControlPadPlayScreenEvent) -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showLog by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = uiState.connectionState) {
        if(uiState.connectionState != ConnectionState.NONE)
            snackbarHostState.showSnackbar(uiState.connectionState.toString())
    }

    // When device back button is pressed
    BackHandler {
        onUiEvent(ControlPadPlayScreenEvent.OnBackPress)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .height(bottomBarHeight)
                    .background(MaterialTheme.colorScheme.primary)
            ){
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(0.7f)
                        .padding(start = 10.dp),

                ){
                    Icon(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clickable { onUiEvent(ControlPadPlayScreenEvent.OnBackPress) },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                    Text(
                        text = uiState.hostAddress,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(10.dp)
                        .clip(shape = RoundedCornerShape(50.dp))
                ) {

                    if(controlPad.logging) {

                        IconButton(
                            onClick = {
                                showLog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "LogIcon",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    if (!uiState.isConnected && uiState.connectionType != ConnectionType.UDP ) {
                        IconButton(
                            onClick = {

                                onUiEvent(
                                    ControlPadPlayScreenEvent.OnConnectClick
                                )


                                // The viewModel will never call connection?.setup()
                                // if the connectionType is Bluetooth_LE and Bluetooth is not enabled.
                                // Note: The latest value of isBluetoothEnabled is only updated when the
                                // ControlPadPlayScreenEvent.OnConnectClick() event is triggered.

                                // Do not use a LaunchedEffect to display the Bluetooth disabled message.
                                // LaunchedEffect only triggers once for the same key, meaning the message
                                // would only be shown the first time the user taps "Connect" while Bluetooth
                                // is disabled. If the user taps "Connect" multiple times, the message will
                                // not be displayed again.

                                // Additionally, using isBluetoothEnabled as a key for LaunchedEffect will not
                                // solve this issue since its value does not change between successive taps
                                // unless Bluetooth is enabled.

                                if((uiState.connectionType == ConnectionType.BLUETOOTH_LE || uiState.connectionType == ConnectionType.BLUETOOTH) && !uiState.isBluetoothEnabled){
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Please Enable bluetooth")
                                    }
                                }
                            },
                            enabled = !uiState.isConnecting,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),

                            ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isConnecting)
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(35.dp),
                                    )
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "PlayIcon",
                                )
                            }
                        }
                    }
                    //Show "Disconnect" button only if connected
                    if(uiState.isConnected || uiState.connectionType == ConnectionType.UDP) {
                        IconButton(
                            onClick = {
                                onUiEvent(
                                    ControlPadPlayScreenEvent.OnDisconnectClick
                                )
                            },
                            // disable button when connectionType is UDP
                            enabled = uiState.connectionType != ConnectionType.UDP,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            )

                        ) { Icon(
                            painter = painterResource(id = R.drawable.ic_power),
                            contentDescription = "PowerIcon",
                        ) }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(uiState.controlPadBackgroundColor.toULong()))
                .padding(innerPadding)
        ){
            uiState.controlPadItems.forEach {controlPadItem ->
                if (controlPadItem.itemType == ItemType.SWITCH) {

                    val switchProperties = SwitchProperties.fromJson(controlPadItem.properties)

                    ControlPadSwitch(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        properties = switchProperties,
                        checked = uiState.switchStates[controlPadItem.id] ?: false,
                        showControls = false,
                        onCheckedChange = {
                            onUiEvent(
                                ControlPadPlayScreenEvent.OnSwitchCheckedChange(
                                    id = controlPadItem.itemIdentifier,
                                    idLong = controlPadItem.id,
                                    checked = it
                                )
                            )
                        }

                    )
                }

                else if (controlPadItem.itemType == ItemType.SLIDER) {

                    val sliderProperties = SliderProperties.fromJson(controlPadItem.properties)
                    //var value by remember { mutableFloatStateOf(sliderProperties.minValue) }

                    ControlPadSlider(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        showControls = false,
                        properties = sliderProperties,
                        value = uiState.sliderStates[controlPadItem.id] ?: sliderProperties.minValue,
                        onValueChange = {
                            onUiEvent(
                                ControlPadPlayScreenEvent.OnSliderValueChange(
                                    id = controlPadItem.itemIdentifier,
                                    idLong = controlPadItem.id,
                                    value = it
                                )
                            )
                        }
                    )
                }

                else if (controlPadItem.itemType == ItemType.STEP_SLIDER) {

                    val stepSliderProperties = StepSliderProperties.fromJson(controlPadItem.properties)
                    var value by remember { mutableFloatStateOf(stepSliderProperties.minValue) }

                    ControlPadStepSlider(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        showControls = false,
                        properties = stepSliderProperties,
                        value = value,
                        onValueChange = {
                            value = it
                            onUiEvent(
                                ControlPadPlayScreenEvent.OnSliderValueChange(
                                    id = controlPadItem.itemIdentifier,
                                    idLong = controlPadItem.id,
                                    value = it
                                )
                            )
                        }
                    )
                }

                else if(controlPadItem.itemType == ItemType.LABEL){
                    ControlPadLabel(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        showControls = false,
                        properties = LabelProperties.fromJson(controlPadItem.properties),
                    )
                }

                else if(controlPadItem.itemType == ItemType.BUTTON){

                    ControlPadButton(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        showControls = false,
                        properties = ButtonProperties.fromJson(controlPadItem.properties),
                        onPressed = { onUiEvent(ControlPadPlayScreenEvent.OnButtonPress(controlPadItem.itemIdentifier)) },
                        onRelease = { onUiEvent(ControlPadPlayScreenEvent.OnButtonRelease(controlPadItem.itemIdentifier)) },
                        onClick = {onUiEvent(ControlPadPlayScreenEvent.OnButtonClick(controlPadItem.itemIdentifier))}
                    )
                }

                else if(controlPadItem.itemType == ItemType.DPAD){

                    ControlPadDpad(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        showControls = false,
                        properties = DpadProperties.fromJson(controlPadItem.properties),
                        onPressed = { dpadButton ->
                            onUiEvent(
                                ControlPadPlayScreenEvent.OnDpadButtonPress(
                                    id = controlPadItem.itemIdentifier,
                                    dPadButton = dpadButton
                                )
                            )
                        },
                        onRelease = { dpadButton ->
                            onUiEvent(
                                ControlPadPlayScreenEvent.OnDpadButtonRelease(
                                    id = controlPadItem.itemIdentifier,
                                    dPadButton = dpadButton
                                )
                            )
                        },
                        onClick = { dpadButton ->
                            onUiEvent(
                                ControlPadPlayScreenEvent.OnDpadButtonClick(
                                    id = controlPadItem.itemIdentifier,
                                    dPadButton = dpadButton
                                )
                            )
                        }
                    )
                }

                else if(controlPadItem.itemType == ItemType.JOYSTICK){

                    ControlPadJoyStick(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        showControls = false,
                        properties = JoyStickProperties.fromJson(controlPadItem.properties),
                        onMove = {x,y ->
                            onUiEvent(ControlPadPlayScreenEvent.OnJoyStickMove(id = controlPadItem.itemIdentifier, x = x, y = y))
                        }
                    )
                }

                else if(controlPadItem.itemType == ItemType.STEERING_WHEEL){
                    ControlPadSteeringWheel(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        showControls = false,
                        properties = SteeringWheelProperties.fromJson(controlPadItem.properties),
                        onRotate = {
                            onUiEvent(ControlPadPlayScreenEvent.OnSteeringWheelRotate(id = controlPadItem.itemIdentifier, angle = it))
                        }
                    )
                }

                else if(controlPadItem.itemType == ItemType.LED){
                    ControlPadLED(
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        state = uiState.ledStates[controlPadItem.id] ?: LEDSTATE.OFF,
                        showControls = false,
                        properties = LEDProperties.fromJson(controlPadItem.properties),
                    )
                }

                else if(controlPadItem.itemType == ItemType.GAUGE){
                    ControlPadGauge(
                        modifier = Modifier.size(250.dp),
                        value = uiState.gaugeStates[controlPadItem.id] ?: 0f,
                        offset = controlPadItem.offset,
                        rotation = controlPadItem.rotation,
                        scale = controlPadItem.scale,
                        showControls = false,
                        properties = GaugeProperties.fromJson(controlPadItem.properties),
                    )
                }

            }



            //Lock the pad if not connected to any server
            if(!uiState.isConnected && uiState.connectionType != ConnectionType.UDP ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                        .zIndex(1f)
                        .pointerInput(Unit) {
                            // Consume all touch events to block input to underlying items
                            detectTapGestures {}
                        },
                    contentAlignment = Alignment.Center
                ){
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            modifier = Modifier.size(50.dp),
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "LockIcon",
                            tint = MaterialTheme.colorScheme.onError
                        )
                        Text(
                            text = "No Connection",
                            color = MaterialTheme.colorScheme.onError,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

        }
    }


    var showBottomSheet by remember { mutableStateOf(false) }

    showBottomSheet = uiState.connectionState == ConnectionState.BLUETOOTH_ADVERTISEMENT_SUCCESS


    if(showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                onUiEvent(ControlPadPlayScreenEvent.OnDisconnectClick)
                showBottomSheet = false
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
            ) {
                Text("Service UUID")
                Text("4fbfc1d7-f509-44ab-afe1-62ea40a4b111")
                Spacer(Modifier.height(10.dp))
                Text("Waiting for Connection")
                LinearProgressIndicator()
                Text("Connect and Subscribe to\n Following characteristic")
                Text("dc3f5274-33ba-48de-8246-43bf8985b323")
                Button(
                    onClick = {
                        onUiEvent(ControlPadPlayScreenEvent.OnDisconnectClick)
                        showBottomSheet = false
                    }
                ) { Text("Cancel") }
            }
        }
    }


    if(showLog){
        ModalBottomSheet(
            onDismissRequest = { showLog = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            Log(
                logState = uiState.logState,
            )
        }

    }


}

@Composable
private fun Log(
    modifier: Modifier = Modifier,
    logState: List<LogEvent>,
) {

    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState
    ) {

        item {
            if(logState.isEmpty()){
                Text(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "No Log Entries"
                )
            }
        }

        items(items = logState){ logEvent ->

            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(5.dp),
                    text = logEvent.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )

                Text(
                    modifier = Modifier.padding(5.dp),
                    text = logEvent.message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    LaunchedEffect(logState.size) {
        if (logState.isNotEmpty()) {
            listState.scrollToItem(logState.lastIndex)
        }
    }

}



@Preview(showBackground = true)
@Composable
private fun LogPreview() {
    DroidPadTheme {
        Surface {
            Log(
                logState = listOf(
                    LogEvent(timestamp = "10:10:10", message = "message 1"),
                    LogEvent(timestamp = "10:10:11", message = "message 2"),
                    LogEvent(timestamp = "10:10:12", message = "message 3 message 3 \n message 3 "),
                    LogEvent(timestamp = "10:10:13", message = "message 4"),
                )
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ControlPadPlayScreenContentPreview(modifier: Modifier = Modifier) {


    val controlPad = ControlPad(
        id = 100,
        name = "myController",
        orientation = Orientation.LANDSCAPE,
    )

    val controlPadItems = listOf(
        ControlPadItem(
            id = 1000,
            itemIdentifier = "label1",
            controlPadId = controlPad.id,
            itemType = ItemType.SWITCH,
        ),
        ControlPadItem(
            id = 1003,
            offsetX = 200f,
            offsetY = 400f,
            itemIdentifier = "slider1",
            controlPadId = controlPad.id,
            itemType = ItemType.SLIDER,
        )
    )


    var uiState by remember { mutableStateOf(
        ControlPadPlayScreenState(
        controlPadItems = controlPadItems,
        connectionState = ConnectionState.WEBSOCKET_CONNECTED,
        connectionType = ConnectionType.WEBSOCKET,
            logState = mutableStateListOf(LogEvent(message = "hello")),
        isConnecting = false,
        isConnected = true,
        hostAddress = "org.mosquitto.org:80807",
        controlPadBackgroundColor = controlPad.backgroundColor
    )
    ) }

/*    LaunchedEffect(Unit){
        ConnectionState.entries.forEach {
            delay(500)
            uiState = uiState.copy(
                connectionState = it
            )

        }
    }*/

    DroidPadTheme {
        ControlPlayScreenContent(
            controlPad = ControlPad(
                id = 100,
                name = "myController",
                orientation = Orientation.LANDSCAPE,
                logging = true
            ),
            uiState = uiState,
            onUiEvent = {}
        )
    }
}

