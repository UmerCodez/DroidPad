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

package com.github.umer0586.droidpad.ui.screens.aboutscreen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.droidpad.ui.theme.DroidPadTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackPress: (() -> Unit)? = null,
) {

    val context = LocalContext.current
    val isPreviewMode = LocalInspectionMode.current

    BackHandler {
        onBackPress?.invoke()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                onBackPress?.invoke()
                            },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "BackIcon"
                    )
                },
                title = { Text("About") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.3f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize(0.7f)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DroidPad",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = "v${if(isPreviewMode) "x.y.z" else getAppVersion(context)}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.7f),
            ){
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Developed By",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = "Umer Farooq",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .clickable {
                                if(!isPreviewMode){
                                    val intent = Intent(Intent.ACTION_SENDTO)
                                    intent.setData(Uri.parse("mailto:")) // only email apps should handle this
                                    intent.putExtra(Intent.EXTRA_EMAIL, "umerfarooq2383@gmail.com")
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")

                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            .padding(10.dp)
                        ,
                        text = "umerfarooq2383@gmail.com",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(16.dp),
                    text = "License : GPL v3",
                    style = MaterialTheme.typography.titleLarge
                )
            }


        }
    }

}



private fun getAppVersion(context: Context) : String{
    val versionName = try {
        context.applicationContext.packageManager
            .getPackageInfo(context.packageName, 0).versionName ?: "Unknown"

    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
    return versionName
}

@Preview
@Composable
fun AboutScreenPreview() {
    DroidPadTheme {
        AboutScreen()
    }
}