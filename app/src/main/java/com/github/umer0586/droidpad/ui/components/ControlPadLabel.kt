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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.data.LabelProperties
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme


@Composable
fun ControlPadLabel(
    modifier: Modifier = Modifier,
    offset: Offset = Offset.Zero,
    rotation: Float = 0f,
    scale: Float = 1f,
    transformableState: TransformableState? = null,
    properties: LabelProperties = LabelProperties(),
    showActionExpander: Boolean = true,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
){

    ControlPadItemBase(
        modifier = modifier,
        offset = offset,
        rotation = rotation,
        scale = scale,
        transformableState = transformableState,
        showActionExpander = showActionExpander,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
    ){
        Text(
            modifier = Modifier.padding(10.dp),
            maxLines = 1,
            text = properties.text
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun ControlPadLabelItemPreview(){
    DroidPadTheme {
        Box(modifier = Modifier.size(150.dp).padding(10.dp)){
            ControlPadLabel(
                modifier = Modifier.align(Alignment.Center),
                offset = Offset.Zero,
                rotation = 0f,
                scale = 1f,
                showActionExpander = true,
            )
        }
    }
}
