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

package com.github.umer0586.droidpad.ui.screens


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.ui.screens.aboutscreen.AboutScreen
import com.github.umer0586.droidpad.ui.screens.connectionconfigscreen.ConnectionConfigScreen
import com.github.umer0586.droidpad.ui.screens.connectionconfigscreen.ConnectionConfigScreenViewModel
import com.github.umer0586.droidpad.ui.screens.controlpadbuilderscreen.ControlPadBuilderScreen
import com.github.umer0586.droidpad.ui.screens.controlpadbuilderscreen.ControlPadBuilderScreenViewModel
import com.github.umer0586.droidpad.ui.screens.controlpadplayscreen.ControlPadPlayScreen
import com.github.umer0586.droidpad.ui.screens.controlpadplayscreen.ControlPadPlayScreenViewModel
import com.github.umer0586.droidpad.ui.screens.controlpadsscreen.ControlPadsScreen
import com.github.umer0586.droidpad.ui.screens.controlpadsscreen.ControlPadsScreenViewModel
import com.github.umer0586.droidpad.ui.screens.newcontrolpadscreen.NewControlPadScreen
import com.github.umer0586.droidpad.ui.screens.newcontrolpadscreen.NewControlPadScreenViewModel
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

object Route{

    //Class names represents routes and there constructor arguments are arguments to those routes

    @Serializable
    object ControlPadListScreen

    @Serializable
    object NewControlPadScreen

    @Serializable
    object AboutScreen

    @Serializable
    data class ControlPadPlayScreen(val controlPad: ControlPad)

    @Serializable
    data class ConnectionConfigScreen(val controlPadId: Long)

    @Serializable
    data class ControlPadBuilderScreen(val controlPad: ControlPad)

}




@Composable
fun NavScreen(
    onExitClick: (() -> Unit)? = null,
) {
    val navController = rememberNavController()

    // ViewModels must be initialized here to ensure a single instance is used.
    // If they are initialized inside composable<Route>, a new ViewModel instance
    // will be created each time navController.navigate() is called for that route.
    val controlPadsScreenViewModel = hiltViewModel<ControlPadsScreenViewModel>()
    val controlPadBuilderScreenViewModel = hiltViewModel<ControlPadBuilderScreenViewModel>()
    val controlPadPlayScreenViewModel = hiltViewModel<ControlPadPlayScreenViewModel>()
    val connectionConfigScreenViewModel = hiltViewModel<ConnectionConfigScreenViewModel>()
    val newControlPadScreenViewModel = hiltViewModel<NewControlPadScreenViewModel>()

    val context = LocalContext.current
    val versionName = try {
        context.packageManager
            .getPackageInfo(context.packageName, 0).versionName ?: "Unknown"

    } catch (e: PackageManager.NameNotFoundException) {
         "Unknown"
    }

    NavHost(
        navController = navController,
        startDestination = Route.ControlPadListScreen
    ) {

        composable<Route.AboutScreen> {
            AboutScreen(
                version = versionName,
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                },
                onEmailClick = {
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
            )
        }

        composable<Route.ControlPadListScreen> {

            ControlPadsScreen(
                appVersion = versionName,
                viewModel = controlPadsScreenViewModel,
                onCreateClick = {
                    navController.navigateTo(Route.NewControlPadScreen)
                },
                onBuildClick = { controlPad ->
                    navController.navigateTo(Route.ControlPadBuilderScreen(controlPad))
                },
                onSettingClick = { controlPad ->
                    navController.navigateTo(Route.ConnectionConfigScreen(controlPad.id))
                },
                onPlayClick = { controlPad ->
                    navController.navigateTo(Route.ControlPadPlayScreen(controlPad))
                },
                onExitClick = {
                    onExitClick?.invoke()
                },
                onAboutClick = {
                    navController.navigateTo(Route.AboutScreen)
                },
                onShareClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    if(intent.resolveActivity(context.packageManager) != null){
                        intent.data = Uri.parse("https://www.github.com/umer0586/DroidPad")
                        context.startActivity(Intent.createChooser(intent,"Select Browser"))
                    } else {
                        Toast.makeText(context,"No browser found", Toast.LENGTH_SHORT).show()
                    }
                }


            )
        }

        composable<Route.NewControlPadScreen> {

            NewControlPadScreen(
                viewModel = newControlPadScreenViewModel,
                onControlPadCreated = {
                    navController.navigateTo(Route.ControlPadListScreen)
                },
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                }
            )

        }


        composable<Route.ControlPadBuilderScreen>(
            typeMap = mapOf(
                typeOf<ControlPad>() to CustomNavType.ControlPadType
            )
        ) { navBackStackEntry ->

            val controlPadBuilderScreen = navBackStackEntry.toRoute<Route.ControlPadBuilderScreen>()
            val controlPad = controlPadBuilderScreen.controlPad

            ControlPadBuilderScreen(
                viewModel = controlPadBuilderScreenViewModel,
                controlPad = controlPad,
                onSaveClick = {
                    navController.navigateTo(Route.ControlPadListScreen)
                },
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                }
            )
        }

        composable<Route.ConnectionConfigScreen>(
            typeMap = mapOf(
                typeOf<ControlPad>() to CustomNavType.ControlPadType
            )
        ) { navBackStackEntry ->
            val connectionConfigScreen = navBackStackEntry.toRoute<Route.ConnectionConfigScreen>()
            ConnectionConfigScreen(
                viewModel = connectionConfigScreenViewModel,
                controlPadId = connectionConfigScreen.controlPadId,
                onConfigSaved = {
                    navController.navigateTo(Route.ControlPadListScreen)
                },
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                }
            )
        }

        composable<Route.ControlPadPlayScreen>(
            typeMap = mapOf(
                typeOf<ControlPad>() to CustomNavType.ControlPadType
            )
        ) { navBackStackEntry ->
            val controlPadPlayScreenRoute = navBackStackEntry.toRoute<Route.ControlPadPlayScreen>()
            val controlPad = controlPadPlayScreenRoute.controlPad

            ControlPadPlayScreen(
                viewModel = controlPadPlayScreenViewModel,
                controlPad = controlPad,
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                }
            )
        }

    }

}

private fun <T : Any> NavHostController.navigateTo(route: T){
//    navigate(route)
    navigate(route){
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(graph.findStartDestination().id) {
               // saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            //restoreState = true

    }

}