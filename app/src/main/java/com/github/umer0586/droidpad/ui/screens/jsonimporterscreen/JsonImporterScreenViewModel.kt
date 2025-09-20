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

package com.github.umer0586.droidpad.ui.screens.jsonimporterscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.ExternalData
import com.github.umer0586.droidpad.data.repositories.JsonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JsonImporterScreenState(
    val downloading: Boolean = false,
    val showErrorSheet: Boolean = false,
    val showDownloaderSheet: Boolean = false,
    val error: String = "Undefined"
)

sealed interface JsonImporterScreenEvent {
    data object OnBackPress : JsonImporterScreenEvent
    data class OnJsonImportedFromFile(val jsonContent: String) : JsonImporterScreenEvent
    data object OnImportFromLinkClick : JsonImporterScreenEvent
    data class OnDownloadClick(val link: String) : JsonImporterScreenEvent
    data object OnErrorSheetDismissRequest : JsonImporterScreenEvent
    data object OnDownloaderSheetDismissRequest : JsonImporterScreenEvent
}

@HiltViewModel
class JsonImporterScreenViewModel @Inject constructor(
    private val jsonRepository: JsonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JsonImporterScreenState())
    val uiState = _uiState.asStateFlow()

    private val tag = javaClass.simpleName

    init {
        Log.d(tag, "init : ${hashCode()}")
    }


    private var _onExternalDataAvailable: ((ExternalData) -> Unit)? = null
    fun onExternalDataAvailable(callback: (ExternalData) -> Unit) {
        _onExternalDataAvailable = callback
    }


    fun onEvent(event: JsonImporterScreenEvent) {
        when (event) {

            is JsonImporterScreenEvent.OnJsonImportedFromFile -> {
                val externalData = convertToExternalData(event.jsonContent)
                externalData?.also { data ->
                    _onExternalDataAvailable?.invoke(data)
                }
            }

            is JsonImporterScreenEvent.OnImportFromLinkClick -> {
                _uiState.update {
                    it.copy(showDownloaderSheet = true)
                }
            }

            is JsonImporterScreenEvent.OnDownloadClick -> {
                _uiState.update {
                    it.copy(
                        downloading = true
                    )
                }
                viewModelScope.launch {
                    try {
                        val jsonString = jsonRepository.fetchRemoteJson(event.link)

                        _uiState.update {
                            it.copy(showDownloaderSheet = false)
                        }

                        val externalData = convertToExternalData(jsonString)


                        externalData?.also { data ->
                            _onExternalDataAvailable?.invoke(data)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                        _uiState.update {
                            it.copy(
                                showErrorSheet = true,
                                showDownloaderSheet = false,
                                error = e.message ?: "Unknown error"
                            )
                        }

                    } finally {
                        _uiState.update {
                            it.copy(downloading = false)
                        }
                    }
                }
            }

            is JsonImporterScreenEvent.OnErrorSheetDismissRequest -> {
                _uiState.update {
                    it.copy(showErrorSheet = false)
                }
            }

            is JsonImporterScreenEvent.OnDownloaderSheetDismissRequest -> {
                _uiState.update {
                    it.copy(showDownloaderSheet = false)
                }
            }

            else -> {}
        }

    }


    private fun convertToExternalData(jsonString: String): ExternalData? {

        var externalData: ExternalData? = null

        try {

            externalData = ExternalData.fromJson(jsonString)

            _uiState.update {
                it.copy(showErrorSheet = false)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update {
                it.copy(
                    showErrorSheet = true,
                    error = e.message ?: "Unknown error",
                )
            }
        }

        return externalData
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(tag, "onCleared : ${hashCode()}")

    }

}