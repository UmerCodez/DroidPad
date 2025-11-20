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

package com.github.umer0586.droidpad.ui.screens


import android.view.Window
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.umer0586.droidpad.data.ExternalData
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.ui.screens.aboutscreen.AboutScreen
import com.github.umer0586.droidpad.ui.screens.connectionconfigscreen.ConnectionConfigScreen
import com.github.umer0586.droidpad.ui.screens.controlpadbuilderscreen.ControlPadBuilderScreen
import com.github.umer0586.droidpad.ui.screens.controlpadimporterscreen.ControlPadImporterScreen
import com.github.umer0586.droidpad.ui.screens.controlpadplayscreen.ControlPadPlayScreen
import com.github.umer0586.droidpad.ui.screens.controlpadsscreen.ControlPadsScreen
import com.github.umer0586.droidpad.ui.screens.jsonimporterscreen.JsonImporterScreen
import com.github.umer0586.droidpad.ui.screens.newcontrolpadscreen.NewControlPadScreen
import com.github.umer0586.droidpad.ui.screens.preferencescreen.PreferenceScreen
import com.github.umer0586.droidpad.ui.screens.qrgeneratorscreen.QrCodeGeneratorScreen
import com.github.umer0586.droidpad.ui.screens.qrscannerscreen.QRCodeScannerScreen
import com.github.umer0586.droidpad.ui.screens.sensorsscreen.SensorsScreen
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
    data class ControlPadBuilderScreen(val controlPad: ControlPad, val tempOpen:Boolean = false, val externalData: ExternalData? = null)

    @Serializable
    data class QRCodeScreen(val controlPad: ControlPad)

    @Serializable
    object QRScannerScreen

    @Serializable
    data class ImporterScreen(val externalData: ExternalData)

    @Serializable
    object JsonImporterScreen

    @Serializable
    object PreferenceScreen

    @Serializable
    data class SensorsScreen(val controlPad: ControlPad)

}




@Composable
fun NavScreen(
    onExitClick: (() -> Unit)? = null,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.ControlPadListScreen
    ) {

        composable<Route.AboutScreen> {
            AboutScreen(
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                }
            )
        }

        composable<Route.ControlPadListScreen> {

            ControlPadsScreen(

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
                onQRGenerateClick = { controlPad ->
                    navController.navigateTo(Route.QRCodeScreen(controlPad))
                },
                onQrScannerClick = {
                    navController.navigateTo(Route.QRScannerScreen)
                },
                onExitClick = {
                    onExitClick?.invoke()
                },
                onAboutClick = {
                    navController.navigateTo(Route.AboutScreen)
                },
                onImportJsonClick = {
                    navController.navigateTo(Route.JsonImporterScreen)
                },
                onPreferenceClick = {
                    navController.navigateTo(Route.PreferenceScreen)
                },
                onAttachSensorsClick = { controlPad ->
                    navController.navigateTo(Route.SensorsScreen(controlPad))
                }

            )
        }

        composable<Route.NewControlPadScreen> {

            NewControlPadScreen(
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
                typeOf<ControlPad>() to CustomNavType.ControlPadType,
                typeOf<ExternalData?>() to CustomNavType.ExternalDataType
            )
        ) { navBackStackEntry ->

            val controlPadBuilderScreen = navBackStackEntry.toRoute<Route.ControlPadBuilderScreen>()
            val controlPad = controlPadBuilderScreen.controlPad
            val tempOpen = controlPadBuilderScreen.tempOpen
            val externalData = controlPadBuilderScreen.externalData


            ControlPadBuilderScreen(
                controlPad = controlPad,
                onSaveClick = {
                    navController.navigateTo(Route.ControlPadListScreen)
                },
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                },
                tempOpen = tempOpen,
                externalData = externalData,
                onTempOpenCompleted = { externalData ->
                    //navController.navigateTo(Route.QRScannerScreen)
                    if(externalData != null)
                        navController.navigateTo(Route.ImporterScreen(externalData))
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
                controlPad = controlPad,
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                }
            )
        }

        composable<Route.QRCodeScreen>(
            typeMap = mapOf(
                typeOf<ControlPad>() to CustomNavType.ControlPadType
            )
        ) { navBackStackEntry ->
            val qRCodeScreenRoute = navBackStackEntry.toRoute<Route.QRCodeScreen>()
            val controlPad = qRCodeScreenRoute.controlPad

            QrCodeGeneratorScreen(
                controlPad = controlPad,
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                }
            )
        }

        composable<Route.QRScannerScreen> {

            QRCodeScannerScreen(
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                },
                onExternalDataAvailable = { externalData ->
                    navController.navigateTo(Route.ImporterScreen(externalData))
                }

            )
        }

        composable<Route.ImporterScreen>(
            typeMap = mapOf(
                typeOf<ExternalData>() to CustomNavType.ExternalDataType
            )
        ) { navBackStackEntry ->

            val importerScreenRoute = navBackStackEntry.toRoute<Route.ImporterScreen>()
            val externalData = importerScreenRoute.externalData

            ControlPadImporterScreen(

                externalData = externalData,
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                },
                onControlPadReady = { controlPad ->
                    navController.navigateTo(Route.ControlPadPlayScreen(controlPad))
                },
                onBuilderScreenResRequired = { externalData ->
                    navController.navigateTo(
                        Route.ControlPadBuilderScreen(
                            controlPad = externalData.controlPad,
                            tempOpen = true,
                            externalData = externalData
                        )
                    )
                }

            )
        }

        composable<Route.JsonImporterScreen> {
            JsonImporterScreen(
                onBackPress = {
                    navController.navigateTo(Route.ControlPadListScreen)
                },
                onExternalDataAvailable = { externalData ->
                    navController.navigateTo(Route.ImporterScreen(externalData))
                }
            )
        }

        composable<Route.PreferenceScreen> {
            PreferenceScreen(
                onBackClick = {
                    navController.navigateTo(Route.ControlPadListScreen)
                }
            )
        }

        composable<Route.SensorsScreen>(
            typeMap = mapOf(
                typeOf<ControlPad>() to CustomNavType.ControlPadType
            )
        ){ navBackStackEntry ->

            val sensorsScreenRoute = navBackStackEntry.toRoute<Route.SensorsScreen>()
            val controlPad = sensorsScreenRoute.controlPad

            SensorsScreen(
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