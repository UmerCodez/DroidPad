package com.github.umer0586.droidpad.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.anastr.speedometer.PointerSpeedometer
import com.github.anastr.speedometer.components.cneter.SvCenterCircle
import com.github.anastr.speedometer.components.indicators.PointIndicator
import com.github.anastr.speedometer.components.indicators.SpindleIndicator
import com.github.anastr.speedometer.components.indicators.TriangleIndicator
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay

@Composable
fun Gauge(
    value: Float,
    modifier: Modifier = Modifier,
    minValue: Float = 0f,
    maxValue: Float = 50f,
    unit: String = "Km/h",
    needle: Boolean = true,
    color: Color =  Color(0xFF4D4D3E),
) {
    val barWidth = 10.dp

    val currentValue by animateFloatAsState(
        targetValue = value.coerceIn(minValue,maxValue),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )


    PointerSpeedometer(
        modifier = modifier,
        speed = currentValue,
        minSpeed = minValue,
        maxSpeed = maxValue,
        barWidth = barWidth,
        unitUnderSpeed = true,
        backgroundCircleColor = color,
        barColor = color.opposite,
        marksColor = color.contrast,
        speedUnitAlignment = if(needle) Alignment.BottomCenter else Alignment.Center,
        speedText = {
            BasicText(
                // Display integer if currentValue is whole number else display with 2 decimal places

                text = if (currentValue == currentValue.toInt().toFloat()) {
                    currentValue.toInt().toString()
                } else {
                    "%.2f".format(currentValue)
                },
                style = MaterialTheme.typography.displayMedium.copy(color = color.contrast),
                autoSize = TextAutoSize.StepBased(
                    minFontSize = MaterialTheme.typography.labelLarge.fontSize,
                    maxFontSize = MaterialTheme.typography.displayMedium.fontSize,
                )
            )
        },
        unitText = {
            Text(
                text = unit,
                color = color.contrast
            )
        },
        indicator = {
            if (needle) {
                SpindleIndicator(
                    color = color.opposite
                )
            }
            else {
                TriangleIndicator(
                    modifier = Modifier.padding(top = 20.dp),
                    color = color.opposite
                )
            }
            // Circular point on bar
            PointIndicator(
                pointRadius = barWidth * .5f + 1.dp,
                backPointRadius = barWidth * .5f + 8.dp,
                centerY = barWidth * .5f,
                color = color.opposite
            )
        },
        centerContent = {
            if(needle){
                SvCenterCircle(
                    size = 24.dp,
                    color = color.opposite
                )
            }
        },
        tickLabel = { _, tickSpeed ->
            Text(
                text = tickSpeed.toInt().toString(),
                color = color.contrast
            )
        },
        ticks = getTicks(minValue,maxValue, divisions = 5)

    )
}




@Preview
@Composable
fun GaugePreview() {
    Gauge(
        modifier = Modifier.size(250.dp) ,
        value = 1000f,
        minValue = 0f,
        maxValue = 1000f,
        needle = true,
        unit = "Km/h",
        color = Color(0xFF60603D),

    )
}

@Preview
@Composable
fun GaugeAnimationPreview() {
    var value by remember { mutableFloatStateOf(0f) }
    val minValue = 0f
    val maxValue = 50f

    LaunchedEffect(Unit) {
        repeat(5){
            value = (minValue.toInt()..maxValue.toInt()).random().toFloat()
            delay(1000)
        }
    }

    Gauge(
        modifier = Modifier.size(250.dp),
        minValue = minValue,
        maxValue = maxValue,
        needle = true,
        value = value,
    )
}


private fun getTicks(minValue: Float, maxValue: Float, divisions: Int): PersistentList<Float> {
    require(divisions > 1) { "divisions must be greater than 1" }
    val step = (maxValue - minValue) / (divisions - 1)
    return persistentListOf<Float>().builder().apply {
        for (i in 0 until divisions) {
            val value = minValue + i * step
            val normalized = (value - minValue) / (maxValue - minValue)
            add(normalized)
        }
    }.build()
}

private val Color.contrast: Color
    get() {
    // Calculating the perceptive luminance - human eye favors green color...
    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue)
    // Returning black for bright colors, white for dark colors
    return if (luminance > 0.5) Color.Black else Color.White
}

private val Color.opposite: Color
    get() {
        val red = 1.0f - this.red
        val green = 1.0f - this.green
        val blue = 1.0f - this.blue
        return Color(red, green, blue, alpha)
    }
