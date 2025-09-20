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


package com.github.umer0586.droidpad.data.util

import androidx.compose.ui.geometry.Offset
import com.github.umer0586.droidpad.data.Resolution

fun getNewScaleForBuilderScreenOnThisDevice(thisDeviceBuilderScreenRes: Resolution, otherDeviceBuilderScreenRes: Resolution): Float {
    // Calculate ratios for both dimensions
    val widthRatio = thisDeviceBuilderScreenRes.width.toFloat() / otherDeviceBuilderScreenRes.width.toFloat()
    val heightRatio = thisDeviceBuilderScreenRes.height.toFloat() / otherDeviceBuilderScreenRes.height.toFloat()

    // Return the smaller ratio to ensure content fits in both dimensions
    return minOf(widthRatio, heightRatio)
}

fun calculateCenteredOffset(
    thisDeviceBuilderScreenRes: Resolution, // Resolution of the builder screen importing the control pad
    otherDeviceBuilderScreenRes: Resolution // Resolution of the builder screen that exported the control pad
): Offset {
    // Calculate the scale factor to fit the control pad into the new screen
    val scale = getNewScaleForBuilderScreenOnThisDevice(thisDeviceBuilderScreenRes, otherDeviceBuilderScreenRes)

    // Calculate the scaled dimensions of the control pad
    val scaledWidth = otherDeviceBuilderScreenRes.width * scale
    val scaledHeight = otherDeviceBuilderScreenRes.height * scale

    // Calculate the offset to center the control pad
    val offsetX = (thisDeviceBuilderScreenRes.width - scaledWidth) / 2f
    val offsetY = (thisDeviceBuilderScreenRes.height - scaledHeight) / 2f

    // Return the centered offset
    return Offset(offsetX, offsetY)
}


