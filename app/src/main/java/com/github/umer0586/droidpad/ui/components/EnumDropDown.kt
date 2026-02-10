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


package com.github.umer0586.droidpad.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <reified T : Enum<T>> EnumDropdown(
    modifier: Modifier = Modifier,
    selectedValue: T,
    label: String,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(50),
    crossinline onValueSelected: (T) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    val enumValues = enumValues<T>() // Retrieve all entries of the enum type

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = dropdownExpanded,
        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
            label = { Text(label) },
            enabled = enabled,
            value = selectedValue.name,
            onValueChange = { newValue ->
                onValueSelected(enumValues.first { it.name == newValue })
            },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
            shape = shape
        )

        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
        ) {
            enumValues.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        onValueSelected(item)
                        dropdownExpanded = false
                    }
                )
            }
        }
    }
}

private enum class Item {
    Item1, Item2, Item3, Item4
}
@Preview(showBackground = true)
@Composable
private fun EnumDropdownPreview() {
    DroidPadTheme {
        Box(Modifier.fillMaxSize()){
            EnumDropdown<Item>(
                modifier = Modifier.align(Alignment.Center),
                enabled = false,
                selectedValue = Item.Item1,
                label = "Label",
                onValueSelected = {}
            )
        }
    }
}