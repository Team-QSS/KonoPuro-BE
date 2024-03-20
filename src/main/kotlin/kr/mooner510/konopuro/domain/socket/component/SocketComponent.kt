package kr.mooner510.konopuro.domain.socket.component

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.AuthTokenListener
import com.corundumstudio.socketio.AuthTokenResult
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIONamespace
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.listener.DisconnectListener
import kr.mooner510.konopuro.domain.socket.data.RawChat
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SocketComponent(
    server: SocketIOServer
) {
    private val log = LoggerFactory.getLogger(SocketComponent::class.java)

    private var namespace: SocketIONamespace? = null

    init {
        this.namespace = server.addNamespace("/socket")
        namespace?.let {
            it.addConnectListener(onConnected())
            it.addDisconnectListener(onDisconnected())
            it.addAuthTokenListener(onAuthToken())
            it.addEventListener("chat", RawChat::class.java, onChatReceived())
        }
    }

    private fun onConnected(): ConnectListener {
        return ConnectListener { client: SocketIOClient ->
            val handshakeData = client.handshakeData
            log.trace("Client[{}] - Connected to chat module through '{}'", client.sessionId.toString(), handshakeData.url)
        }
    }

    private fun onDisconnected(): DisconnectListener {
        return DisconnectListener { client: SocketIOClient ->
            log.trace("Client[{}] - Disconnected from chat module.", client.sessionId.toString())
        }
    }

    private fun onChatReceived(): DataListener<RawChat> {
        return DataListener<RawChat> { client: SocketIOClient, data: RawChat, ackSender: AckRequest? ->
            log.trace("Client[{}] - Received chat message '{}'", client.sessionId.toString(), data)
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