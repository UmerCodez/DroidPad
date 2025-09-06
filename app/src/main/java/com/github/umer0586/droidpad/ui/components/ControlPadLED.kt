package com.github.umer0586.droidpad.ui.components

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.data.LEDProperties
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme
import kotlinx.coroutines.delay

enum class LEDSTATE {
    ON,OFF,BLINK
}
@Composable
fun ControlPadLED(
    modifier: Modifier = Modifier,
    offset: Offset = Offset.Zero,
    rotation: Float = 0f,
    scale: Float = 1f,
    properties: LEDProperties = LEDProperties(),
    state: LEDSTATE = LEDSTATE.OFF,
    transformableState: TransformableState? = null,
    showActionExpander: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
) {

    ControlPadItemBase(
        modifier = modifier,
        offset = offset,
        rotation = rotation,
        scale = scale,
        showActionExpander = showActionExpander,
        transformableState = transformableState,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick
    ){

        var ledState by remember { mutableStateOf(false) }

        when (state) {
            LEDSTATE.BLINK -> {
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(500)
                        ledState = !ledState
                    }
                }
            }
            LEDSTATE.ON -> {
                ledState = true
            }
            LEDSTATE.OFF -> {
                ledState = false
            }
        }

       LED(
           modifier = Modifier.padding(10.dp),
           color = Color(properties.color),
           state = ledState,
       )

    }

}

@Preview
@Composable
fun ControlPadLEDPreview() {
    DroidPadTheme {
        ControlPadLED(
            showActionExpander = false,
            state = LEDSTATE.BLINK,
        )
    }
}
