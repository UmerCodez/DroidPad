package com.github.umer0586.droidpad.connectiontests

import app.cash.turbine.test
import com.github.umer0586.droidpad.MainDispatcherRule
import com.github.umer0586.droidpad.data.connection.ConnectionState
import com.github.umer0586.droidpad.data.connection.WebsocketConnection
import com.github.umer0586.droidpad.data.connectionconfig.WebsocketConfig
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.net.InetSocketAddress

@Ignore("This Tests times out with 'gradlew test' but works fine when run manually")
@RunWith(JUnit4::class)
class WebsocketConnectionTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `test websocket connection`() = runTest {

        val serverReady = CompletableDeferred<Unit>()

        val websocketServer = object : WebSocketServer(InetSocketAddress("127.0.0.1", 9002)) {

            override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {

            }

            override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {

            }

            override fun onMessage(conn: WebSocket?, message: String?) {
                assertEquals("xyz", message)

            }

            override fun onError(conn: WebSocket?, ex: Exception?) {

            }

            override fun onStart() {
                println("websocket server started")
                serverReady.complete(Unit)
            }

        }

        val testDispatcher = mainDispatcherRule.testDispatcher

        launch(testDispatcher) {
            websocketServer.start()
        }

        serverReady.await()

        val job = launch(testDispatcher) {
            val websocketConfig = WebsocketConfig("127.0.0.1", 9002)
            val websocketConnection = WebsocketConnection(websocketConfig, testDispatcher)
            websocketConnection.setup()
            websocketConnection.sendData("xyz")
            websocketConnection.tearDown()
        }


        job.join()

    }

    @Test
    fun `test connection timeout()`() = runTest{

        val testDispatcher = mainDispatcherRule.testDispatcher

        val job = launch(testDispatcher) {
            val websocketConfig = WebsocketConfig("127.0.0.1", 8090)
            val websocketConnection = WebsocketConnection(websocketConfig, testDispatcher)
            websocketConnection.setup()
            websocketConnection.connectionState.test {
                assertEquals(ConnectionState.WEBSOCKET_CONNECTION_TIMEOUT,  awaitItem())
            }
        }

        job.join()
    }
}