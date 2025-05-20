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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.R
import com.github.umer0586.droidpad.data.DPAD_STYLE
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme

enum class DPAD_BUTTON {
    UP,DOWN,LEFT,RIGHT
}

@Composable
fun Dpad(
    modifier: Modifier = Modifier,
    style: DPAD_STYLE = DPAD_STYLE.CIRCULAR,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    buttonColor: Color = MaterialTheme.colorScheme.onPrimary,
    useClickAction: Boolean = false,
    onClick: ((DPAD_BUTTON) -> Unit)? = null,
    onPressed: ((DPAD_BUTTON) -> Unit)? = null,
    onRelease: ((DPAD_BUTTON) -> Unit)? = null,
    enabled: Boolean = true,
) {
    val dpadSize: Dp = 170.dp // do not change this value, changing this would break ui exported via QR or JSON
    val buttonSize = (dpadSize/2.dp).dp

    if(style == DPAD_STYLE.CIRCULAR) {
        Box(
            modifier = modifier
                .size(dpadSize)
                .clip(CircleShape)
                .background(backgroundColor)

        ) {
            val iconOffsetAmount = 10
            ButtonExtended(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(buttonSize),
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.Transparent
                ),
                useClickAction = useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.UP) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.UP) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.UP) }
            ) {
                Icon(
                    modifier = Modifier.offset(y = (-iconOffsetAmount).dp),
                    painter = painterResource(R.drawable.ic_up_arrow),
                    tint = buttonColor,
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
                    containerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.Transparent
                ),
                useClickAction = useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.LEFT) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.LEFT) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.LEFT) }
            ) {
                Icon(
                    modifier = Modifier.offset(x = (-iconOffsetAmount).dp),
                    painter = painterResource(R.drawable.ic_left_arrow),
                    tint = buttonColor,
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
                    containerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.Transparent
                ),
                useClickAction = useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.RIGHT) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.RIGHT) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.RIGHT) }
            ) {
                Icon(
                    modifier = Modifier.offset(x = (iconOffsetAmount).dp),
                    painter = painterResource(R.drawable.ic_right_arrow),
                    tint = buttonColor,
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
                    containerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.Transparent
                ),
                useClickAction = useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.DOWN) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.DOWN) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.DOWN) }
            ) {
                Icon(
                    modifier = Modifier.offset(y = (iconOffsetAmount).dp),
                    painter = painterResource(R.drawable.ic_down_arrow),
                    tint = buttonColor,
                    contentDescription = "down"
                )
            }
        }
    } else if(style == DPAD_STYLE.CROSS){
        Box(
            modifier = modifier
                .size(dpadSize)
                .drawBehind {
                    drawRoundedPlus(this, color = backgroundColor)
                }

        ) {
            val iconOffsetAmount = 15
            ButtonExtended(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(buttonSize),
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.Transparent
                ),
                useClickAction = useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.UP) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.UP) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.UP) }
            ) {
                Icon(
                    modifier = Modifier.offset(y = (-iconOffsetAmount).dp),
                    painter = painterResource(R.drawable.ic_up_arrow),
                    tint = buttonColor,
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
                    containerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.Transparent
                ),
                useClickAction = useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.LEFT) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.LEFT) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.LEFT) }
            ) {
                Icon(
                    modifier = Modifier.offset(x = (-iconOffsetAmount).dp),
                    painter = painterResource(R.drawable.ic_left_arrow),
                    tint = buttonColor,
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
                    containerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.Transparent
                ),
                useClickAction = useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.RIGHT) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.RIGHT) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.RIGHT) }
            ) {
                Icon(
                    modifier = Modifier.offset(x = iconOffsetAmount.dp),
                    painter = painterResource(R.drawable.ic_right_arrow),
                    tint = buttonColor,
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
                    containerColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.Transparent
                ),
                useClickAction = useClickAction,
                onClick = { onClick?.invoke(DPAD_BUTTON.DOWN) },
                onPressed = { onPressed?.invoke(DPAD_BUTTON.DOWN) },
                onRelease = { onRelease?.invoke(DPAD_BUTTON.DOWN) }
            ) {
                Icon(
                    modifier = Modifier.offset(y = iconOffsetAmount.dp),
                    painter = painterResource(R.drawable.ic_down_arrow),
                    tint = buttonColor,
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

private fun drawRoundedPlus(drawScope: DrawScope, color: Color = Color.Black) = with(drawScope) {
    val width = size.width
    val height = size.height
    val armWidth = width / 3f
    val armHeight = height / 3f
    val cornerRadius = armWidth / 2f

    drawRoundRect(
        color = color,
        topLeft = Offset((width - armWidth) / 2f, 0f),
        size = androidx.compose.ui.geometry.Size(armWidth, height),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )

    drawRoundRect(
        color = color,
        topLeft = Offset(0f, (height - armHeight) / 2f),
        size = androidx.compose.ui.geometry.Size(width, armHeight),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
}

@Preview(showBackground = true)
@Composable
private fun DpadPreview(modifier: Modifier = Modifier) {
    DroidPadTheme {
        Dpad()
    }
}

@Preview(showBackground = true)
@Composable
private fun DpadInteractivePreview(modifier: Modifier = Modifier) {
    DroidPadTheme {
        Box(Modifier.fillMaxSize()){
            val dpadAction = remember { mutableStateOf("Action") }
            Text(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 150.dp),
                text = dpadAction.value
            )

            Dpad(
                modifier = Modifier.align(Alignment.Center),
                style = DPAD_STYLE.CROSS,
                enabled = true,
                onPressed = {
                    dpadAction.value = "Pressed: $it"
                },
                onRelease = {
                    dpadAction.value = "Released: $it"
                },
                useClickAction = false,
                onClick = {
                    dpadAction.value = "Clicked: $it"
                }
            )
        }
    }
}
