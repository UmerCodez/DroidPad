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


package com.github.umer0586.droidpad.data.util

import android.graphics.Bitmap
import android.util.Base64
import com.github.umer0586.droidpad.data.ExternalData
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object QRCodeGenerator {



    @Throws(Exception::class)
    suspend fun createQrCode(qrData: ExternalData): Bitmap? {


        // Dispatchers.Default: Suitable for CPU-intensive operations like compression
        val qrCodeImage = withContext(Dispatchers.Default) {
            val compressedByteArray = DeflateCompression.compress(qrData.toJson())
            val compressedStringBase64 =
                Base64.encodeToString(compressedByteArray, Base64.DEFAULT)
            val qrCodeImage = BarcodeEncoder().encodeBitmap(
                compressedStringBase64,
                BarcodeFormat.QR_CODE,
                1000,
                1000
            )

            return@withContext qrCodeImage
        }

        return qrCodeImage
    }

    // Helper method to be used in Previews
    @Throws(WriterException::class)
    fun createQRCode(content: String, size: Int = 1000): Bitmap {
        return BarcodeEncoder().encodeBitmap(content, BarcodeFormat.QR_CODE, size, size)
    }

}

