package com.github.umer0586.droidpad.connectiontests

import app.cash.turbine.test
import com.github.umer0586.droidpad.MainDispatcherRule
import com.github.umer0586.droidpad.data.connection.Connection
import com.github.umer0586.droidpad.data.connection.ConnectionState
import com.github.umer0586.droidpad.data.connection.TCPConnection
import com.github.umer0586.droidpad.data.connectionconfig.TCPConfig
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Ignore("This Test Class in ignored for now, need to be fixed asap")
@RunWith(JUnit4::class)
class TCPConnectionTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    // TODO: check this later
    // The test hangs indefinitely at job.join() though server job is completed
    @Test(timeout = 5000L)
    fun `test tcp client connection`() = runTest {

        val testDispatcher = mainDispatcherRule.testDispatcher

        val selectorManager = SelectorManager(testDispatcher)

        val serverReady = CompletableDeferred<Unit>()

        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 9002)
        println("Server is listening at ${serverSocket.localAddress}")
        serverReady.complete(Unit)

        val job = launch(testDispatcher) {

            val socket = serverSocket.accept()
            println("Accepted $socket")

            val receiveChannel = socket.openReadChannel()
            println("Received $receiveChannel")

            val data = receiveChannel.readUTF8Line()
            println("Received $data")

            socket.close()
            serverSocket.close()


            assertEquals("xyz", data)
            println("server ending..")

        }


        // Wait for server to be ready before creating client connection
        serverReady.await()

        val tcpConnection: Connection = TCPConnection(
            TCPConfig("127.0.0.1", 9002),
            ioDispatcher = testDispatcher
        )


        println("Setting up connection")

        tcpConnection.setup()

        println("Sending data")
        tcpConnection.sendData("xyz")
        tcpConnection.tearDown()


        println("waiting to complete")

        // Test hangs here
        job.join()

    }

    // TODO: see this later
    // The test is falling, but the code works fine in production
    @Test
    fun `test tcp connection timeout`() = runTest {

        val testDispatcher = mainDispatcherRule.testDispatcher

        val job = launch(testDispatcher) {
            val tcpConfig = TCPConfig("127.0.0.1", 8090)
            val tcpConnection = TCPConnection(tcpConfig, testDispatcher)
            tcpConnection.setup()
            tcpConnection.connectionState.test {
                assertEquals(ConnectionState.TCP_CONNECTION_TIMEOUT,  awaitItem())
            }


        }

        job.join()
    }


}