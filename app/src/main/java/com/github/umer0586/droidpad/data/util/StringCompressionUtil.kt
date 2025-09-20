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

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.Inflater

object DeflateCompression {
    /**
     * Compresses a string using Deflater with maximum compression level
     * @param input String to compress
     * @return ByteArray of compressed data
     */
    fun compress(input: String): ByteArray {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val deflater = Deflater(Deflater.BEST_COMPRESSION)
        deflater.setInput(bytes)
        deflater.finish()

        val outputStream = ByteArrayOutputStream(bytes.size)
        val buffer = ByteArray(1024)

        while (!deflater.finished()) {
            val count = deflater.deflate(buffer)
            outputStream.write(buffer, 0, count)
        }

        deflater.end()
        outputStream.close()

        return outputStream.toByteArray()
    }

    /**
     * Decompresses a compressed byte array
     * @param compressedInput Compressed byte array
     * @return Original decompressed string
     */
    fun decompress(compressedInput: ByteArray): String {
        val inflater = Inflater()
        inflater.setInput(compressedInput)

        val outputStream = ByteArrayOutputStream(compressedInput.size)
        val buffer = ByteArray(1024)

        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }

        inflater.end()
        outputStream.close()

        return outputStream.toString(Charsets.UTF_8.name())
    }
}



object GzipCompression {
    /**
     * Custom GZIPOutputStream that allows setting compression level
     */
    private class CustomGZIPOutputStream(
        out: OutputStream,
        level: Int
    ) : GZIPOutputStream(out) {
        init {
            this.def.setLevel(level)
        }
    }

    /**
     * Compresses a string using GZIP compression
     * @param string The string to compress
     * @param level Compression level (0-9). Default is Deflater.DEFAULT_COMPRESSION
     *             0 = no compression
     *             1 = best speed
     *             9 = best compression
     * @return ByteArray of compressed data
     * @throws IllegalArgumentException if compression level is invalid
     * @throws Exception if compression fails
     */
    fun compress(
        string: String,
        level: Int = Deflater.BEST_COMPRESSION
    ): ByteArray {
        require(level in -1..9) { "Compression level must be between -1 and 9" }

        return ByteArrayOutputStream().use { byteStream ->
            CustomGZIPOutputStream(byteStream, level).use { gzipStream ->
                gzipStream.write(string.toByteArray(StandardCharsets.UTF_8))
            }
            byteStream.toByteArray()
        }
    }

    /**
     * Decompresses a GZIP compressed byte array back to a string
     * @param compressedData The compressed data as ByteArray
     * @return The decompressed string
     * @throws Exception if decompression fails
     */
    fun decompress(compressedData: ByteArray): String {
        return ByteArrayInputStream(compressedData).use { byteStream ->
            GZIPInputStream(byteStream).use { gzipStream ->
                val output = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (gzipStream.read(buffer).also { length = it } > 0) {
                    output.write(buffer, 0, length)
                }
                String(output.toByteArray(), StandardCharsets.UTF_8)
            }
        }
    }
}