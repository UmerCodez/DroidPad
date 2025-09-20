package com.github.umer0586.droidpad.ui.components

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.github.umer0586.droidpad.data.GaugeProperties
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme


@Composable
fun ControlPadGauge(
    value: Float,
    modifier: Modifier = Modifier,
    offset: Offset = Offset.Zero,
    rotation: Float = 0f,
    scale: Float = 1f,
    transformableState: TransformableState? = null,
    showControls: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    properties: GaugeProperties = GaugeProperties(),
) {
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
        Gauge(
            value = value,
            minValue = properties.minValue,
            maxValue = properties.maxValue,
            needle = properties.needle,
            unit = properties.unit,
            color = Color(properties.color)
        )
    }

}

@Preview
@Composable
fun ControlPadGaugePreview() {
    DroidPadTheme {
        ControlPadGauge(
            value = 50f,
            properties = GaugeProperties(
                minValue = 0f,
                maxValue = 100f,
                color = MaterialTheme.colorScheme.primary.value
            )
        )
    }
}
