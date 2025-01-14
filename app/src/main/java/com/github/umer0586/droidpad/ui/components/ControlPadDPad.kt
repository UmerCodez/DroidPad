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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.R
import com.github.umer0586.droidpad.data.DpadProperties
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

enum class DPAD_BUTTON {
    UP,DOWN,LEFT,RIGHT
}

@Composable
fun ControlPadDpad(
    modifier: Modifier = Modifier,
    offset: Offset,
    rotation: Float,
    scale: Float,
    properties: DpadProperties = DpadProperties(),
    enabled: Boolean = true,
    transformableState: TransformableState? = null,
    showControls: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onClick: ((DPAD_BUTTON) -> Unit)? = null,
    onPressed: ((DPAD_BUTTON) -> Unit)? = null,
    onRelease: ((DPAD_BUTTON) -> Unit)? = null,
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

        val dpadSize = 170.dp
        val buttonSize = (dpadSize/2.dp).dp

        Box(
            modifier = Modifier
                .size(dpadSize)
                .clip(CircleShape)
                .background(Color(properties.backgroundColor))

        ) {
            ButtonExtended(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(buttonSize),
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color(properties.backgroundColor),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color(properties.backgroundColor)
                ),
                useClickAction = properties.useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.UP) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.UP) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.UP) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_up_arrow),
                    tint = Color(properties.buttonColor),
                    contentDescription = "up"
                )
            }


            ButtonExtended(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(buttonSize),
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color(properties.backgroundColor),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color(properties.backgroundColor)
                ),
                useClickAction = properties.useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.LEFT) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.LEFT) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.LEFT) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_left_arrow),
                    tint = Color(properties.buttonColor),
                    contentDescription = "left"
                )
            }


            ButtonExtended(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(buttonSize),
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color(properties.backgroundColor),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color(properties.backgroundColor)
                ),
                useClickAction = properties.useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.RIGHT) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.RIGHT) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.RIGHT) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_right_arrow),
                    tint = Color(properties.buttonColor),
                    contentDescription = "right"
                )
            }



            ButtonExtended(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(buttonSize),
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color(properties.backgroundColor),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color(properties.backgroundColor)
                ),
                useClickAction = properties.useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.DOWN) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.DOWN) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.DOWN) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_down_arrow),
                    tint = Color(properties.buttonColor),
                    contentDescription = "down"
                )
            }
        }
    }

}

@Composable
private fun ButtonExtended(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    useClickAction: Boolean = false,
    onClick: (() -> Unit)? = null,
    onPressed: (() -> Unit)? = null,
    onRelease: (() -> Unit)? = null,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable() (RowScope.() -> Unit)
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    if (isPressed){
        //Pressed
        if (!useClickAction)
            onPressed?.invoke()
        //Use if + DisposableEffect to wait for the press action is completed
        DisposableEffect(Unit) {
            onDispose {
                //released
                if (!useClickAction)
                    onRelease?.invoke()
            }
        }
    }

    Button(
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        shape = shape,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        onClick = {
            if (useClickAction)
                onClick?.invoke()
        }
    ) {
        content()
    }
}

@Preview
@Composable
fun ControlPadDpadPreview(modifier: Modifier = Modifier) {
    DroidPadTheme {
        ControlPadDpad(
            modifier = modifier,
            offset = Offset.Zero,
            rotation = 0f,
            scale = 1f,
            properties = DpadProperties(
                backgroundColor = MaterialTheme.colorScheme.primary.value,
                buttonColor = MaterialTheme.colorScheme.onPrimary.value,
                useClickAction = true
            )
        )
    }
}