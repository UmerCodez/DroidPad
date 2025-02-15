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


package com.github.umer0586.droidpad.ui.screens.qrscannerscreen

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.droidpad.data.ExternalData
import com.github.umer0586.droidpad.data.util.DeflateCompression
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class QRScannerScreenState(
    val decoding: Boolean = false,
    val decodingSuccess: Boolean = false,
    val decodingFailed: Boolean = false
)

sealed interface QRScannerScreenEvent {
    data class OnQrCodeScanned(val data: String) : QRScannerScreenEvent
    data object OnBackPress : QRScannerScreenEvent
}


class QRScannerScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QRScannerScreenState())
    val uiState = _uiState.asStateFlow()

    private lateinit var qrString: String

    private var _onExternalDataAvailable: ((ExternalData) -> Unit)? = null
    fun onExternalDataAvailable(callback:(ExternalData) -> Unit) {
        _onExternalDataAvailable = callback
    }

    fun onEvent(event: QRScannerScreenEvent) {
        when (event) {
            is QRScannerScreenEvent.OnQrCodeScanned -> {
                viewModelScope.launch {
                    qrString = event.data

                    _uiState.update {
                        it.copy(decoding = true)
                    }

                    try {
                        val externalData = withContext(Dispatchers.Default) {
                            val compressedString = Base64.decode(qrString, Base64.DEFAULT)
                            val json = DeflateCompression.decompress(compressedString)
                            return@withContext ExternalData.fromJson(json)
                        }

                        _onExternalDataAvailable?.invoke(externalData)

                        _uiState.update {
                            it.copy(
                                decodingSuccess = true,
                                decodingFailed = false,
                                decoding = false
                            )
                        }

                    }catch (e: Exception) {
                        _uiState.update {
                            it.copy(
                                decodingFailed = true,
                                decodingSuccess = false,
                                decoding = false
                            )
                        }
                    }


                }
            }

            else -> {}
        }
    }


}