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

package com.github.umer0586.droidpad.ui.screens.sensorsscreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.data.sensor.accelerometer
import com.github.umer0586.droidpad.data.sensor.gyroscope
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme


@Composable
fun SensorsScreen(
    controlPad: ControlPad,
    viewModel: SensorsScreenViewModel = hiltViewModel(),
    onBackPress: (() -> Unit)? = null
) {

    val state by viewModel.uiState.collectAsState()

    SensorScreenContent(
        controlPad = controlPad,
        state = state,
        onEvent = { event ->
            viewModel.onEvent(event)

            if(event is SensorsScreenEvent.OnBackPress)
                onBackPress?.invoke()
        }

    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorScreenContent(
    controlPad: ControlPad,
    state: SensorsScreenState,
    onEvent: (SensorsScreenEvent) -> Unit,
) {

    BackHandler {
        onEvent(SensorsScreenEvent.OnBackPress)
    }

    LaunchedEffect(Unit) {
        onEvent(SensorsScreenEvent.LoadSelectedSensorsForControlPad(controlPad))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                onEvent(SensorsScreenEvent.OnBackPress)
                            },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "BackIcon"
                    )
                },
                title = { Text("Attach Sensors") },
                actions = {
                    IconButton(
                        onClick = { onEvent(SensorsScreenEvent.OnAddSensorClick) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "AddIcon"
                        )
                    }
                }
            )
        }

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
        ) {
            items(state.selectedSensors){
                ListItem(
                    headlineContent = { Text(it.name) },
                    trailingContent = {
                        IconButton(
                            onClick = { onEvent(SensorsScreenEvent.OnSensorDeleteClick(controlPad,it)) }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "BackIcon"
                            )
                        }
                    }
                )
            }
        }

    }

    if(state.showSensorSelector){
        ModalBottomSheet(
            onDismissRequest = { onEvent(SensorsScreenEvent.OnSensorSelectorDismissRequest) },
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
            ) {
                items(state.availableSensors.filter { it !in state.selectedSensors }){ sensor ->

                    var showInfo by remember { mutableStateOf(false) }

                    ListItem(
                        headlineContent = { Text(sensor.name) },
                        trailingContent = {
                            TextButton(
                                onClick = { onEvent(SensorsScreenEvent.OnSensorSelected(controlPad,sensor)) }
                            ) {
                                Text("Attach")
                            }
                        },
                        supportingContent = {

                            Text(
                                modifier = Modifier.clickable { showInfo = !showInfo },
                                text = if(showInfo) "Hide" else "Info",
                                color = MaterialTheme.colorScheme.secondary
                            )

                        }
                    )
                    AnimatedVisibility(visible = showInfo) {
                        Column (Modifier.padding(horizontal = 10.dp, vertical = 10.dp)){
                            Text("Max Range: ${sensor.maximumRange}")
                            Text("Power: ${sensor.power}mA")
                            Text("Resolution: ${sensor.resolution}")
                            Text("Vendor: ${sensor.vendor}")
                            Text("Max Delay: ${sensor.maxDelay}µs")
                            Text("Min Delay: ${sensor.minDelay}µs")
                        }
                    }


                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun SensorScreenContentPreview(modifier: Modifier = Modifier) {

    val state by remember {
        mutableStateOf(
            SensorsScreenState(
                availableSensors = mutableStateListOf(accelerometer, gyroscope),
                selectedSensors = mutableStateListOf(),
                showSensorSelector = true
            )
        )
    }


    DroidPadTheme {
        SensorScreenContent(
            controlPad = ControlPad(
                name = "temp",
                backgroundColor = 0,
                orientation = Orientation.PORTRAIT
            ),
          state = state,
           onEvent = {}
        )
    }

}