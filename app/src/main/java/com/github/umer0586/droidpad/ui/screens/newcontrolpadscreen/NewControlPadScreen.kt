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

package com.github.umer0586.droidpad.ui.screens.newcontrolpadscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.ui.components.EnumDropdown
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

@Composable
fun NewControlPadScreen(
    viewModel: NewControlPadScreenViewModel = hiltViewModel(),
    onControlPadCreated: ((controlPad: ControlPad) -> Unit)? = null,
    onBackPress: (() -> Unit)? = null
){

    viewModel.onControlPadCreated{
        onControlPadCreated?.invoke(it)
    }


    val uiState by viewModel.uiState.collectAsState()

    NewControlPadScreenContent(
        uiState = uiState,
        onUiEvent = { event ->
            viewModel.onEvent(event)

            if(event is NewControlPadScreenEvent.OnBackPress)
                onBackPress?.invoke()

        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewControlPadScreenContent(
    uiState: NewControlPadScreenState,
    onUiEvent: (NewControlPadScreenEvent) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Control Pad") },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .clickable { onUiEvent(NewControlPadScreenEvent.OnBackPress) },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            OutlinedTextField(
                value = uiState.controlPadName,
                isError = uiState.inputError,
                singleLine = true,
                onValueChange = {
                    onUiEvent(
                        NewControlPadScreenEvent.OnControlPadNameChanged(it)
                    )
                },
                label = { Text("Name") },
                shape = RoundedCornerShape(50)
            )

            Spacer(modifier = Modifier.height(20.dp))


            EnumDropdown<Orientation>(
                selectedValue = uiState.controlPadOrientation,
                label = "Orientation",
                onValueSelected = {
                    onUiEvent(NewControlPadScreenEvent.OnControlPadOrientationChanged(it))
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (!uiState.inputError)
                TextButton(
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    onClick = {
                        onUiEvent(NewControlPadScreenEvent.OnCreateClick)
                    },
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Create")
                }
        }
    }




}






@Preview(showBackground = true)
@Composable
fun ControlPadCreationScreenInteractivePreview() {
    DroidPadTheme {

        var uiState by remember {
            mutableStateOf(
                NewControlPadScreenState(
                    controlPadName = "MyControlPad",
                    controlPadOrientation = Orientation.LANDSCAPE
                )
            )
        }

        NewControlPadScreenContent(
            uiState = uiState,
            onUiEvent = { event ->
                when (event) {
                    is NewControlPadScreenEvent.OnControlPadNameChanged -> {
                        uiState = uiState.copy(
                            controlPadName = event.controlPadName,
                            inputError = event.controlPadName.isEmpty()
                        )
                    }

                    is NewControlPadScreenEvent.OnControlPadOrientationChanged -> {
                        uiState = uiState.copy(controlPadOrientation = event.controlPadOrientation)
                    }

                    NewControlPadScreenEvent.OnCreateClick -> {

                    }
                    else -> TODO("Not yet implemented")


                }
            }
        )
    }
}