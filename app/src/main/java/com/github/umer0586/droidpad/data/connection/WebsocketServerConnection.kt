package com.github.umer0586.droidpad.data.connection

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import com.github.umer0586.droidpad.data.connectionconfig.WebsocketServerConfig
import com.github.umer0586.droidpad.data.database.entities.ConnectionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.math.BigInteger
import java.net.BindException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

class WebsocketServerConnection (
    val websocketServerConfig: WebsocketServerConfig,
    val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
): Connection() {
    override val connectionType: ConnectionType
        get() = ConnectionType.WEBSOCKET_SERVER

    val wifiManager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

    private var websocketServer: WsServer? = null


    private val _hostAddress: MutableStateFlow<String?> = MutableStateFlow(null)
    val hostAddress = _hostAddress.asStateFlow()

    override suspend fun setup() = withContext(ioDispatcher) {


        if(websocketServerConfig.listenOnAllInterfaces){
            websocketServer = WsServer(InetSocketAddress("0.0.0.0",websocketServerConfig.port))
        } else {

            if(!wifiManager.isWifiEnabled){
                notifyConnectionState(ConnectionState.WIFI_NOT_ENABLE)
                return@withContext
            }

            val address = wifiManager.getIp()
            if(address == null){
                notifyConnectionState(ConnectionState.FAILED_TO_OBTAIN_IP_ADDRESS)
                return@withContext
            }
            websocketServer = WsServer(InetSocketAddress(address,websocketServerConfig.port))
        }


        try {
            websocketServer?.start()
        }catch (e: Exception) {
            e.printStackTrace()
            //notifyConnectionState(ConnectionState.WEBSOCKET_SERVER_ERROR)
        }
    }

    override suspend fun sendData(data: String) = withContext<Unit>(ioDispatcher) {
        try {
            websocketServer?.broadcast(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun tearDown() = withContext<Unit>(ioDispatcher) {
        try {
            websocketServer?.stop()
            websocketServer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        notifyConnectionState(ConnectionState.WEBSOCKET_SERVER_STOPPED)
    }

    private inner class WsServer(address: InetSocketAddress) : WebSocketServer(address) {

        override fun onOpen(
            conn: WebSocket?,
            handshake: ClientHandshake?
        ) {

        }

        override fun onClose(
            conn: WebSocket?,
            code: Int,
            reason: String?,
            remote: Boolean
        ) {

        }

        override fun onMessage(conn: WebSocket?, message: String?) {
            message?.also { msg ->
                scope.launch {
                    notifyReceivedData(msg)
                }
            }
        }

        override fun onError(conn: WebSocket?, ex: Exception?) {

            // if conn is null than we have error related to server, not related to specific client
            if (conn == null) {
                notifyConnectionState(ConnectionState.WEBSOCKET_SERVER_ERROR)

                if(ex is BindException){
                    notifyConnectionState(ConnectionState.PORT_NO_ALREADY_IN_USE)
                }

            }
            ex?.printStackTrace()
        }

        override fun onStart() {
            notifyConnectionState(ConnectionState.WEBSOCKET_SERVER_STARTED)
            _hostAddress.value = "ws://${address.hostName}:${address.port}"
        }

    }

}

@Suppress("DEPRECATION")
private fun WifiManager.getIp(): String? {
    var ipAddress = this.connectionInfo.ipAddress

    // Convert little-endian to big-endianif needed
    if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
        ipAddress = Integer.reverseBytes(ipAddress)
    }
    val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
    val ipAddressString: String? = try {
        InetAddress.getByAddress(ipByteArray).hostAddress
    } catch (ex: UnknownHostException) {
        null
    }
    return ipAddressString
}