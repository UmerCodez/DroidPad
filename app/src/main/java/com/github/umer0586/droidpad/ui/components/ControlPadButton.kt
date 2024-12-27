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

package com.github.umer0586.droidpad.ui.components

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.data.properties.ButtonProperties
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

@Composable
fun ControlPadButton(
    modifier: Modifier = Modifier,
    offset: Offset,
    rotation: Float,
    scale: Float,
    properties: ButtonProperties = ButtonProperties(),
    enabled: Boolean = true,
    transformableState: TransformableState? = null,
    showControls: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onPressed: (() -> Unit)? = null,
    onRelease: (() -> Unit)? = null
){

    ControlPadItemBase(
        modifier = modifier,
        offset = offset,
        rotation = rotation,
        scale = scale,
        transformableState = transformableState,
        showControls = showControls,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick
    ) {
/*        if(properties.useIcon){
            IconButton(
                modifier = Modifier.size(100.dp).padding(10.dp),
                enabled = enabled,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color(properties.buttonColor),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color(properties.buttonColor)
                ),
                onClick = { onClick?.invoke() }
            ) {
                Icon(
                    modifier = Modifier.size(35.dp),
                    painter = painterResource(ButtonProperties.getIconById(properties.iconId)),
                    contentDescription = properties.text,
                    tint = Color(properties.iconColor),
                )
            }

        }else {
            TextButton(
                modifier = Modifier.size(100.dp).padding(10.dp),
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color(properties.buttonColor),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color(properties.buttonColor)
                ),
                onClick = { onClick?.invoke() },
            ) { Text(properties.text)}
        }*/
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        if (isPressed){
            //Pressed
            onPressed?.invoke()
            //Use if + DisposableEffect to wait for the press action is completed
            DisposableEffect(Unit) {
                onDispose {
                    //released
                    onRelease?.invoke()
                }
            }
        }

        Button(
            modifier = Modifier
                .size(100.dp)
                .padding(10.dp),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = Color(properties.buttonColor),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = Color(properties.buttonColor)
            ),
            interactionSource = interactionSource,
            onClick = {
                //onClick?.invoke()
            }
        ) {
            if(properties.useIcon){
                Icon(
                    modifier = Modifier.size(35.dp),
                    painter = painterResource(ButtonProperties.getIconById(properties.iconId)),
                    contentDescription = properties.text,
                    tint = Color(properties.iconColor),
                )
            } else {
                Text(properties.text)
            }
        }
    }

}
@PreviewLightDark
@Preview(showBackground = true)
@Composable
private fun ControlPadButtonPreview(){
    val buttonProperties = ButtonProperties(
        text = "Btn",
        buttonColor = Color(0xFF7D5260).value,
        useIcon = true,
        iconId = 1,
        iconColor = Color(0xFFFFFCFC).value
    )
    DroidPadTheme {
        Box(Modifier.size(100.dp)){
            ControlPadButton(
                properties = buttonProperties,
                modifier = Modifier.align(Alignment.Center),
                offset = Offset.Zero,
                rotation = 0f,
                scale = 1f,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ButtonActionPreview(){

    val buttonProperties = ButtonProperties(
        text = "Btn",
        buttonColor = Color(0xFF7D5260).value,
        useIcon = true,
        iconId = 1,
        iconColor = Color(0xFFFFFCFC).value
    )
    var state by remember { mutableStateOf("Nothing") }
    DroidPadTheme {
        Box(Modifier.fillMaxSize()){

            Text(
                modifier = Modifier.align(Alignment.TopCenter),
                text = state
            )

            ControlPadButton(
                modifier = Modifier.align(Alignment.Center),
                properties = buttonProperties,
                offset = Offset.Zero,
                rotation = 0f,
                scale = 1f,
                onPressed = { state = "Pressed" },
                onRelease = { state = "Released" },
            )

        }
    }

}

