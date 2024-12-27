package com.github.umer0586.droidpad.connectiontests

import app.cash.turbine.test
import com.github.umer0586.droidpad.MainDispatcherRule
import com.github.umer0586.droidpad.data.connection.Connection
import com.github.umer0586.droidpad.data.connection.ConnectionState
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ConnectionStateFlowTest{

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Test to ensure that the ConnectionState flow emits the correct values
    @Test
    fun `test ConnectionState flow`() = runTest {

        val connection = object: Connection() {
            override val connectionType: ConnectionType
                get() = ConnectionType.TCP

            override suspend fun setup() {

                notifyConnectionState(ConnectionState.TCP_CONNECTING)
                notifyConnectionState(ConnectionState.TCP_CONNECTED)
            }

            override suspend fun sendData(data: String) {
                TODO("Not yet implemented")
            }

            override suspend fun tearDown() {
                notifyConnectionState(ConnectionState.TCP_DISCONNECTING)
                notifyConnectionState(ConnectionState.TCP_DISCONNECTED)
            }

        }

        connection.connectionState.test {

            assertEquals(ConnectionState.NONE,awaitItem())

            connection.setup()
            assertEquals(ConnectionState.TCP_CONNECTING,awaitItem())
            assertEquals(ConnectionState.TCP_CONNECTED,awaitItem())

            connection.tearDown()
            assertEquals(ConnectionState.TCP_DISCONNECTING,awaitItem())
            assertEquals(ConnectionState.TCP_DISCONNECTED,awaitItem())
            // Verify no more emissions
            ensureAllEventsConsumed()
        }


    }



}