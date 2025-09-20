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

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.ButtonEvent
import com.github.umer0586.droidpad.data.DPadEvent
import com.github.umer0586.droidpad.data.GaugeEvent
import com.github.umer0586.droidpad.data.GaugeProperties
import com.github.umer0586.droidpad.data.JoyStickEvent
import com.github.umer0586.droidpad.data.LedEvent
import com.github.umer0586.droidpad.data.LogEvent
import com.github.umer0586.droidpad.data.SliderEvent
import com.github.umer0586.droidpad.data.SliderProperties
import com.github.umer0586.droidpad.data.SteeringWheelEvent
import com.github.umer0586.droidpad.data.SwitchEvent
import com.github.umer0586.droidpad.data.connection.BluetoothConnection
import com.github.umer0586.droidpad.data.connection.BluetoothLEConnection
import com.github.umer0586.droidpad.data.connection.Connection
import com.github.umer0586.droidpad.data.connection.ConnectionFactory
import com.github.umer0586.droidpad.data.connection.ConnectionState
import com.github.umer0586.droidpad.data.connection.Mqttv3Connection
import com.github.umer0586.droidpad.data.connection.Mqttv5Connection
import com.github.umer0586.droidpad.data.connection.TCPConnection
import com.github.umer0586.droidpad.data.connection.UDPConnection
import com.github.umer0586.droidpad.data.connection.WebsocketConnection
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.data.repositories.ConnectionConfigRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadSensorRepository
import com.github.umer0586.droidpad.data.repositories.PreferenceRepository
import com.github.umer0586.droidpad.data.sensor.SensorEventProvider
import com.github.umer0586.droidpad.data.util.bluetooth.BluetoothUtil
import com.github.umer0586.droidpad.data.util.vibrator.VibratorUtil
import com.github.umer0586.droidpad.ui.components.DPAD_BUTTON
import com.github.umer0586.droidpad.ui.components.LEDSTATE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject


data class ControlPadPlayScreenState(
    val controlPadItems: List<ControlPadItem> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.NONE,
    val switchStates: SnapshotStateMap<Long,Boolean> = mutableStateMapOf(),
    val sliderStates: SnapshotStateMap<Long,Float> = mutableStateMapOf(),
    val ledStates: SnapshotStateMap<Long, LEDSTATE> = mutableStateMapOf(),
    val logState: SnapshotStateList<LogEvent> = mutableStateListOf(),
    val gaugeStates: SnapshotStateMap<Long, Float> = mutableStateMapOf(),
    val connectionType: ConnectionType = ConnectionType.TCP,
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val controlPadBackgroundColor : Long = Color.Red.value.toLong(),
    val hostAddress: String = "",
    val isBluetoothEnabled: Boolean = false
)
sealed interface ControlPadPlayScreenEvent {
    data object OnConnectClick : ControlPadPlayScreenEvent
    data object OnDisconnectClick : ControlPadPlayScreenEvent
    data class OnSwitchCheckedChange(val id: String, val idLong: Long, val checked: Boolean) : ControlPadPlayScreenEvent
    data class OnSliderValueChange(val id: String, val idLong: Long, val value: Float) : ControlPadPlayScreenEvent
    data class OnButtonPress(val id: String) : ControlPadPlayScreenEvent
    data class OnButtonRelease(val id: String) : ControlPadPlayScreenEvent
    data class OnButtonClick(val id: String) : ControlPadPlayScreenEvent
    data class OnDpadButtonPress(val id: String, val dPadButton: DPAD_BUTTON) : ControlPadPlayScreenEvent
    data class OnDpadButtonRelease(val id: String, val dPadButton: DPAD_BUTTON) : ControlPadPlayScreenEvent
    data class OnDpadButtonClick(val id: String, val dPadButton: DPAD_BUTTON) : ControlPadPlayScreenEvent
    data class OnJoyStickMove(val id: String, val x: Float, val y: Float) : ControlPadPlayScreenEvent
    data class OnSteeringWheelRotate(val id: String, val angle: Float) : ControlPadPlayScreenEvent
    data object OnBackPress : ControlPadPlayScreenEvent
}



@HiltViewModel
class ControlPadPlayScreenViewModel @Inject constructor(
    private val controlPadRepository: ControlPadRepository,
    private val connectionConfigRepository: ConnectionConfigRepository,
    private val connectionFactory: ConnectionFactory,
    private val bluetoothUtil: BluetoothUtil,
    private val preferenceRepository: PreferenceRepository,
    private val controlPadSensorRepository: ControlPadSensorRepository,
    private val sensorEventProvider: SensorEventProvider,
    private val vibratorUtil: VibratorUtil
) : ViewModel() {

    private var _uiState = MutableStateFlow(
        ControlPadPlayScreenState()
    )

    val uiState = _uiState.asStateFlow()

    private var connection: Connection? = null
    private var sendJsonOverBluetooth = false
    private var samplingRate = 200000
    private var vibrate = false

    private val tag = javaClass.simpleName

    init {

        Log.d(tag, "init : ${hashCode()}")
        viewModelScope.launch {
            bluetoothUtil.bluetoothState.collect{ bluetoothState ->
                _uiState.update { it.copy(isBluetoothEnabled = bluetoothState.isEnable) }
            }
        }

        viewModelScope.launch {
            preferenceRepository.preference.collect{ preference->
                sendJsonOverBluetooth = preference.sendJsonOverBluetooth
                samplingRate = preference.sensorSamplingRate
                vibrate = preference.vibrate
            }
        }

        viewModelScope.launch {
            // Even though connection.sendData(data) is safe to call on the main thread,
            // we collect the sensor event flow on the IO dispatcher to avoid frequent
            // execution on the main thread, as sensor events are emitted very frequently.
            sensorEventProvider.events.flowOn(Dispatchers.IO).collect{ sensorEvent ->
                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    sensorEvent.toCsv()
                else
                    sensorEvent.toJson()

                    connection?.sendData(data)

            }
        }


    }


    fun loadControlPadItemsFor(controlPad: ControlPad) {

        viewModelScope.launch {
            val controlPadItems = controlPadRepository.getControlPadItemsOf(controlPad)
            _uiState.value = _uiState.value.copy(
                controlPadItems = controlPadItems,
                controlPadBackgroundColor = controlPad.backgroundColor,
            )


            controlPadRepository.getControlPadItemsOf(controlPad)
                .filter { it.itemType == ItemType.SWITCH }.forEach { switch ->
                uiState.value.switchStates[switch.id] = false
            }

            controlPadRepository.getControlPadItemsOf(controlPad)
                .filter { it.itemType == ItemType.LED }.forEach { led ->
                uiState.value.ledStates[led.id] = LEDSTATE.OFF
            }

            uiState.value.logState.clear()

            controlPadRepository.getControlPadItemsOf(controlPad)
                .filter { it.itemType == ItemType.SLIDER }.forEach { slider ->
                    val sliderProperties = SliderProperties.fromJson(slider.properties)
                    uiState.value.sliderStates[slider.id] = sliderProperties.minValue
                }

            controlPadRepository.getControlPadItemsOf(controlPad)
                .filter { it.itemType == ItemType.GAUGE }.forEach { gauge ->
                    val gaugeProperties = GaugeProperties.fromJson(gauge.properties)
                    uiState.value.gaugeStates[gauge.id] = gaugeProperties.minValue
                }

            connectionConfigRepository.getConfigForControlPad(controlPad.id)
                ?.also { connectionConfig ->
                    connection = connectionFactory.getConnection(connectionConfig, scope = viewModelScope)

                    if(connection?.connectionType == ConnectionType.UDP)
                        connection?.setup()

                    handleIncomingData(controlPad, controlPadItems)

                    _uiState.update {
                        it.copy(
                            connectionType = connectionConfig.connectionType,
                            hostAddress = when(connectionConfig.connectionType){
                                ConnectionType.TCP -> (connection as TCPConnection).tcpConfig.address
                                ConnectionType.UDP -> (connection as UDPConnection).udpConfig.address
                                ConnectionType.WEBSOCKET -> (connection as WebsocketConnection).webSocketConfig.address
                                ConnectionType.MQTT_V5 -> (connection as Mqttv5Connection).mqttConfig.brokerAddress
                                ConnectionType.MQTT_V3 -> (connection as Mqttv3Connection).mqttConfig.brokerAddress
                                ConnectionType.BLUETOOTH_LE -> (connection as BluetoothLEConnection).bluetoothDisplayName
                                ConnectionType.BLUETOOTH -> (connection as BluetoothConnection).bluetoothConfig.remoteDevice?.address ?: "No Device"

                            }
                        )
                    }

                    if(connectionConfig.connectionType == ConnectionType.UDP){
                        viewModelScope.launch {
                            val controlPadSensorsTypes = controlPadSensorRepository.getControlPadSensorsByControlPadId(controlPad.id).map { it.sensorType }
                            sensorEventProvider.provideEventsFor(controlPadSensorsTypes, samplingRate)
                        }
                    }


                    launch {
                        connection?.connectionState?.collect { connectionState ->
                            Log.d("Play", "Connection state: $connectionState")

                            val isConnecting = when(connectionState){
                                ConnectionState.TCP_CONNECTING -> true
                                ConnectionState.WEBSOCKET_CONNECTING ->true
                                ConnectionState.MQTT_CONNECTING -> true
                                ConnectionState.BLUETOOTH_CONNECTING -> true
                                else -> false
                            }


                            val isConnected = when(connectionState){
                                ConnectionState.TCP_CONNECTED -> true
                                ConnectionState.WEBSOCKET_CONNECTED -> true
                                ConnectionState.MQTT_CONNECTED -> true
                                ConnectionState.BLUETOOTH_CLIENT_CONNECTED -> true
                                ConnectionState.BLUETOOTH_CONNECTED -> true
                                else -> false
                            }

                            if (isConnected) {
                                launch {
                                    val controlPadSensorsTypes =
                                        controlPadSensorRepository.getControlPadSensorsByControlPadId(
                                            controlPad.id
                                        ).map { it.sensorType }
                                    sensorEventProvider.provideEventsFor(controlPadSensorsTypes, samplingRate)
                                }
                            } else if(!isConnecting){ // if not connected and not connecting
                                // if not connected and not connecting then it means we are in disconnected state,
                                sensorEventProvider.stopProvidingEvents()
                            }


                            _uiState.update { uiState ->
                                uiState.copy(
                                    isConnecting = isConnecting,
                                    connectionState = connectionState,
                                    isConnected = isConnected
                                )
                            }


                        }
                    }
                }
        }


    }

    fun onEvent(event: ControlPadPlayScreenEvent) {
        when (event) {
            is ControlPadPlayScreenEvent.OnConnectClick -> {

                if(connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH){
                    _uiState.update {
                        it.copy(isBluetoothEnabled = bluetoothUtil.isBluetoothEnabled())
                    }
                    if(!bluetoothUtil.isBluetoothEnabled())
                        return
                }


                viewModelScope.launch {
                    connection?.setup()
                }
            }

            is ControlPadPlayScreenEvent.OnDisconnectClick -> {
                viewModelScope.launch {
                    connection?.tearDown()
                }
                sensorEventProvider.stopProvidingEvents()
            }

            is ControlPadPlayScreenEvent.OnSwitchCheckedChange -> {
                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    SwitchEvent(id = event.id, state = event.checked).toCSV()
                else
                    SwitchEvent(id = event.id, state = event.checked).toJson()

                uiState.value.switchStates[event.idLong] = event.checked

                vibrate()
                viewModelScope.launch {
                    connection?.sendData(data)
                }

            }


            is ControlPadPlayScreenEvent.OnSliderValueChange -> {

                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    SliderEvent(id = event.id, value = event.value).toCSV()
                else
                    SliderEvent(id = event.id, value = event.value).toJson()

                uiState.value.sliderStates[event.idLong] = event.value

                viewModelScope.launch {
                    connection?.sendData(data)
                }
            }

            is ControlPadPlayScreenEvent.OnButtonClick -> {

                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    ButtonEvent(id = event.id, state = "CLICK").toCSV()
                else
                    ButtonEvent(id = event.id, state = "CLICK").toJson()

                vibrate()
                viewModelScope.launch {
                    connection?.sendData(data)
                }
            }

            is ControlPadPlayScreenEvent.OnBackPress -> {
                viewModelScope.launch {
                    connection?.tearDown()
                }
                sensorEventProvider.stopProvidingEvents()
            }

            is ControlPadPlayScreenEvent.OnButtonPress -> {

                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    ButtonEvent(id = event.id, state = "PRESS").toCSV()
                else
                    ButtonEvent(id = event.id, state = "PRESS").toJson()

                vibrate()
                viewModelScope.launch {
                    connection?.sendData(data)
                }
            }
            is ControlPadPlayScreenEvent.OnButtonRelease -> {
                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    ButtonEvent(id = event.id, state = "RELEASE").toCSV()
                else
                    ButtonEvent(id = event.id, state = "RELEASE").toJson()

                vibrate()
                viewModelScope.launch {
                    connection?.sendData(data)
                }
            }

            is ControlPadPlayScreenEvent.OnDpadButtonClick -> {

                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    DPadEvent(id = event.id, button = event.dPadButton, state = "CLICK").toCSV()
                else
                    DPadEvent(id = event.id, button = event.dPadButton, state = "CLICK").toJson()

                vibrate()
                viewModelScope.launch {
                    connection?.sendData(data)
                }
            }
            is ControlPadPlayScreenEvent.OnDpadButtonPress -> {

                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    DPadEvent(id = event.id, button = event.dPadButton, state = "PRESS").toCSV()
                else
                    DPadEvent(id = event.id, button = event.dPadButton, state = "PRESS").toJson()


                vibrate()
                viewModelScope.launch {
                    connection?.sendData(data)
                }
            }
            is ControlPadPlayScreenEvent.OnDpadButtonRelease -> {

                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    DPadEvent(id = event.id, button = event.dPadButton, state = "RELEASE").toCSV()
                else
                    DPadEvent(id = event.id, button = event.dPadButton, state = "RELEASE").toJson()

                vibrate()
                viewModelScope.launch {
                    connection?.sendData(data)
                }
            }

            is ControlPadPlayScreenEvent.OnJoyStickMove -> {
                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    JoyStickEvent(id = event.id, x = event.x, y = event.y).toCSV()
                else
                    JoyStickEvent(id = event.id, x = event.x, y = event.y).toJson()

                viewModelScope.launch {
                    connection?.sendData(data)
                }
            }

            is ControlPadPlayScreenEvent.OnSteeringWheelRotate -> {
                val data = if((connection?.connectionType == ConnectionType.BLUETOOTH_LE || connection?.connectionType == ConnectionType.BLUETOOTH) && !sendJsonOverBluetooth)
                    SteeringWheelEvent(id = event.id, angle = event.angle).toCSV()
                else
                    SteeringWheelEvent(id = event.id, angle = event.angle).toJson()

                viewModelScope.launch {
                    connection?.sendData(data)
                }

            }
        }
    }

    private fun vibrate() {
        if(vibrate){
            vibratorUtil.vibrate()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            connection?.tearDown()
        }

        sensorEventProvider.stopProvidingEvents()
        sensorEventProvider.cleanUp()
        bluetoothUtil.cleanUp()
        Log.d(tag, "onCleared: ${hashCode()}")
    }

    private fun handleIncomingData(controlPad: ControlPad, controlPadItems: List<ControlPadItem>){
        viewModelScope.launch {
            connection?.receivedData?.collect{ jsonString ->

                val jsonElement = try {
                    Json.parseToJsonElement(jsonString)
                }catch (e: Exception){
                    e.printStackTrace()
                    return@collect
                }

                if(jsonElement is JsonObject){

                    try {

                        if ("type" in jsonElement.keys && jsonElement["type"]?.jsonPrimitive?.content == "SWITCH") {
                            val switchEvent = SwitchEvent.fromJson(jsonString)
                            controlPadItems.filter { it.itemType == ItemType.SWITCH }
                                .find { switchItem ->
                                    switchItem.itemIdentifier == switchEvent.id
                                }?.also { switchItem ->
                                    uiState.value.switchStates[switchItem.id] = switchEvent.state
                                }
                        }
                        else if ("type" in jsonElement.keys && jsonElement["type"]?.jsonPrimitive?.content == "SLIDER") {
                            val sliderEvent = SliderEvent.fromJson(jsonString)
                            controlPadItems.filter { it.itemType == ItemType.SLIDER }
                                .find { sliderItem ->
                                    sliderItem.itemIdentifier == sliderEvent.id
                                }?.also { sliderItem ->
                                    val sliderProperties = SliderProperties.fromJson(sliderItem.properties)
                                    uiState.value.sliderStates[sliderItem.id] = sliderEvent.value.coerceIn(sliderProperties.minValue, sliderProperties.maxValue)
                                }
                        }
                        else if("type" in jsonElement.keys && jsonElement["type"]?.jsonPrimitive?.content == "LED"){
                            val ledEvent = LedEvent.fromJson(jsonString)
                            controlPadItems.filter { it.itemType == ItemType.LED }
                                .find { ledItem ->
                                    ledItem.itemIdentifier == ledEvent.id
                                }?.also { ledItem ->
                                    uiState.value.ledStates[ledItem.id] = ledEvent.state
                                }
                        }
                        else if(controlPad.logging || "type" in jsonElement.keys && jsonElement["type"]?.jsonPrimitive?.content == "LOG"){

                            val timestamp = SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(Date())
                            val logEvent = LogEvent.fromJson(jsonString).copy(timestamp = timestamp)

                            uiState.value.logState.add(logEvent)
                        }
                        else if("type" in jsonElement.keys && jsonElement["type"]?.jsonPrimitive?.content == "GAUGE"){
                            val gaugeEvent = GaugeEvent.fromJson(jsonString)
                            controlPadItems.filter { it.itemType == ItemType.GAUGE }
                                .find { gaugeItem ->
                                    gaugeItem.itemIdentifier == gaugeEvent.id
                                }?.also { gaugeItem ->
                                    uiState.value.gaugeStates[gaugeItem.id] = gaugeEvent.value
                                }
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        }
    }



}

