package kr.mooner510.konopuro.domain.socket.component

import com.corundumstudio.socketio.*
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.listener.DisconnectListener
import com.fasterxml.jackson.databind.ObjectMapper
import kr.mooner510.konopuro.domain.socket.data.RawData
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class SocketComponent(
    server: SocketIOServer,
) {
    private var namespace: SocketIONamespace? = null

    @Bean
    fun nameSpace(): SocketIONamespace {
        return namespace!!
    }


    init {
        this.namespace = server.addNamespace("/socket-io")
        namespace?.let {
            it.addConnectListener(onConnected())
            it.addDisconnectListener(onDisconnected())
            it.addAuthTokenListener(onAuthToken())
            server.addEventInterceptor { namespaceClient, s, anies, ackRequest ->
                println("Event Listen($s): ${namespaceClient.sessionId}")
            }
            it.addEventListener("chat", RawData::class.java, onChatReceived())
        }
    }

    private fun onConnected(): ConnectListener {
        return ConnectListener { client: SocketIOClient ->
            val authorization = client.handshakeData.httpHeaders.get("Authorization", "")
            if (authorization.isBlank()) {
                println("Auth: $authorization")
                client.disconnect()
                return@ConnectListener
            }
            val handshakeData = client.handshakeData
            println("Client[${client.sessionId}] - Connected to chat module through '${handshakeData.url}'")
        }
    }

    private fun onDisconnected(): DisconnectListener {
        return DisconnectListener { client: SocketIOClient ->
            val authorization = client.handshakeData.httpHeaders.get("Authorization", "")
            if (authorization.isBlank()) return@DisconnectListener
            println("Client[${client.sessionId}] - Disconnected from chat module.")
        }
    }

    private fun onChatReceived(): DataListener<RawData> {
        return DataListener<RawData> { client: SocketIOClient, data: RawData, ackSender: AckRequest? ->
            println("Client[${client.sessionId}] - Received chat message '${data}'")
            namespace!!.broadcastOperations.sendEvent("chat", data)
        }
    }

    private fun onAuthToken(): AuthTokenListener {
        return AuthTokenListener { data: Any, _: SocketIOClient ->
            println(data)
            AuthTokenResult.AuthTokenResultSuccess
        }
    }
}