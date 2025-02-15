package com.github.umer0586.droidpad

import com.github.umer0586.droidpad.data.util.DeflateCompression
import com.github.umer0586.droidpad.data.util.GzipCompression
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Base64


@RunWith(JUnit4::class)
class CompressionTest{

    val jsonString2 = "{\"controlPad\":{\"name\":\"MyControlPad\",\"orientation\":\"PORTRAIT\"},\"controlPadItems\":[{\"itemIdentifier\":\"switch\",\"controlPadId\":1,\"offsetX\":276.21954,\"offsetY\":86.53557,\"itemType\":\"SWITCH\",\"properties\":\"{\\\"trackColor\\\":18436547675319959552,\\\"thumbColor\\\":18390783329902264320}\"},{\"itemIdentifier\":\"slider\",\"controlPadId\":1,\"offsetX\":188.23854,\"offsetY\":282.27948,\"itemType\":\"SLIDER\",\"properties\":\"{\\\"thumbColor\\\":18436547675319959552,\\\"trackColor\\\":18436547675319959552}\"},{\"itemIdentifier\":\"label\",\"controlPadId\":1,\"offsetX\":48.47654,\"offsetY\":47.995384,\"itemType\":\"LABEL\"},{\"itemIdentifier\":\"joystick\",\"controlPadId\":1,\"offsetX\":439.39963,\"offsetY\":417.73776,\"scale\":0.6990592,\"itemType\":\"JOYSTICK\",\"properties\":\"{\\\"backgroundColor\\\":18436547675319959552,\\\"handleColor\\\":18390783329902264320}\"},{\"itemIdentifier\":\"button\",\"controlPadId\":1,\"offsetX\":33.32292,\"offsetY\":436.08234,\"itemType\":\"BUTTON\",\"properties\":\"{\\\"textColor\\\":18390783329902264320,\\\"buttonColor\\\":18436547675319959552,\\\"iconColor\\\":18390783329902264320}\"},{\"itemIdentifier\":\"dpad\",\"controlPadId\":1,\"offsetX\":168.9952,\"offsetY\":627.3417,\"scale\":0.71167594,\"rotation\":-1.0875416,\"itemType\":\"DPAD\",\"properties\":\"{\\\"backgroundColor\\\":18436547675319959552,\\\"buttonColor\\\":18390783329902264320}\"}],\"connectionConfig\":{\"controlPadId\":1,\"connectionType\":\"WEBSOCKET\",\"configJson\":\"{}\"}}"
    val jsonString = """ {"controlPad":{"name":"MyControlPad","orientation":"PORTRAIT"},"controlPadItems":[{"itemIdentifier":"switch","controlPadId":1,"offsetX":276.21954,"offsetY":86.53557,"itemType":"SWITCH","properties":"{\"trackColor\":18436547675319959552,\"thumbColor\":18390783329902264320}"},{"itemIdentifier":"slider","controlPadId":1,"offsetX":188.23854,"offsetY":282.27948,"itemType":"SLIDER","properties":"{\"thumbColor\":18436547675319959552,\"trackColor\":18436547675319959552}"},{"itemIdentifier":"label","controlPadId":1,"offsetX":48.47654,"offsetY":47.995384,"itemType":"LABEL"},{"itemIdentifier":"joystick","controlPadId":1,"offsetX":439.39963,"offsetY":417.73776,"scale":0.6990592,"itemType":"JOYSTICK","properties":"{\"backgroundColor\":18436547675319959552,\"handleColor\":18390783329902264320}"},{"itemIdentifier":"button","controlPadId":1,"offsetX":33.32292,"offsetY":436.08234,"itemType":"BUTTON","properties":"{\"textColor\":18390783329902264320,\"buttonColor\":18436547675319959552,\"iconColor\":18390783329902264320}"},{"itemIdentifier":"dpad","controlPadId":1,"offsetX":168.9952,"offsetY":627.3417,"scale":0.71167594,"rotation":-1.0875416,"itemType":"DPAD","properties":"{\"backgroundColor\":18436547675319959552,\"buttonColor\":18390783329902264320}"}],"connectionConfig":{"controlPadId":1,"connectionType":"WEBSOCKET","configJson":"{}"}} """

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Test
    fun `testing Deflate`() = runTest {
        val compressedBytes = DeflateCompression.compress(jsonString)
        println("original len: ${jsonString.length}")
        println("compressed len: ${compressedBytes.size}")
        assert(jsonString == DeflateCompression.decompress(compressedBytes))
        val base64String = Base64.getEncoder().encodeToString(compressedBytes)
        println("base64 len: ${base64String.length}")

    }

    @Test
    fun `testing Gzip`() = runTest {
        val compressedBytes = GzipCompression.compress(jsonString)
        println("original len: ${jsonString.length}")
        println("compressed len: ${compressedBytes.size}")
        assert(jsonString == GzipCompression.decompress(compressedBytes))
        val base64String = Base64.getEncoder().encodeToString(compressedBytes)
        println("base64 len: ${base64String.length}")

    }




}