package com.github.umer0586.droidpad.ui.components.propertieseditor

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.data.LabelProperties
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem

// TODO: Add color choose for label text
@Composable
fun LabelPropertiesEditor(
    controlPadItem: ControlPadItem,
    labelTextMaxLength: Int,
    onLabelPropertiesChange: ((LabelProperties) -> Unit)? = null,
    hasError: ((Boolean) -> Unit)? = null,
) {
    var labelProperties by remember { mutableStateOf(LabelProperties.fromJson(controlPadItem.properties)) }

    OutlinedTextField(
        modifier = Modifier.testTag("labelTextField"),
        singleLine = true,
        value = labelProperties.text,
        isError = labelProperties.text.isEmpty(),
        onValueChange = {

            if(it.isEmpty())
                hasError?.invoke(true)
            else
                hasError?.invoke(false)

            if (it.length <= labelTextMaxLength) {
                labelProperties = labelProperties.copy(text = it)
                onLabelPropertiesChange?.invoke(labelProperties)
            }
        },
        label = { Text("Label Text") },
        shape = RoundedCornerShape(50.dp)
    )
}