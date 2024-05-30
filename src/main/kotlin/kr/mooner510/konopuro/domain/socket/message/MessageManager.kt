package kr.mooner510.konopuro.domain.socket.message

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.AuthTokenListener
import com.corundumstudio.socketio.AuthTokenResult
import com.corundumstudio.socketio.BroadcastOperations
import com.corundumstudio.socketio.ClientOperations
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIONamespace
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.listener.DisconnectListener
import kr.mooner510.konopuro.domain.game.component.GameManager
import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.game.GameRoom
import kr.mooner510.konopuro.domain.socket.data.RawData
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import kr.mooner510.konopuro.global.security.component.TokenProvider
import kr.mooner510.konopuro.global.security.repository.UserRepository
import kr.mooner510.konopuro.global.utils.UUIDParser
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageManager(
    server: SocketIOServer,
    private val userRepository: UserRepository,
    private val tokenProvider: TokenProvider
) {
    private var namespace: SocketIONamespace = server.addNamespace("/socket-io")

    @Bean
    fun nameSpace(): SocketIONamespace {
        return namespace
    }

    init {
        namespace.let {
            it.addConnectListener(onConnected())
            it.addDisconnectListener(onDisconnected())
            it.addAuthTokenListener(onAuthToken())
            server.addEventInterceptor { namespaceClient, s, _, _ ->
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
            println("Client[${client.sessionId}] - Connected to chat module through '${handshakeData.url}' / Authorization: $authorization with session ${client.sessionId}")
            val authKey = tokenProvider.getAccessKey(UUIDParser.transfer(authorization))
            userRepository.updateClientById(client.sessionId, authKey.userId)

            GameManager.findRoomByUser(authKey.userId)?.let {
                if (it.firstPlayer.id == authKey.userId) {
                    it.firstPlayer.client = client.sessionId
                } else {
                    it.secondPlayer.client = client.sessionId
                }
                send(it, RawProtocol(Protocol.Match.RECONNECTED))
            }
        }
    }

    private fun onDisconnected(): DisconnectListener {
        return DisconnectListener { client: SocketIOClient ->
            val authorization = client.handshakeData.httpHeaders.get("Authorization", "")
            if (authorization.isBlank()) return@DisconnectListener
            println("Client[${client.sessionId}] - Disconnected from chat module.")
            userRepository.updateClientToNull(client.sessionId)
            send(GameManager.findRoomByClient(client.sessionId), RawProtocol(Protocol.Match.DISCONNECTED))
        }
    }

    private fun onChatReceived(): DataListener<RawData> {
        return DataListener<RawData> { client: SocketIOClient, data: RawData, _: AckRequest? ->
            println("Client[${client.sessionId}] - Received chat message '${data}'")
            namespace.broadcastOperations.sendEvent("chat", data)
        }
    }

    private fun onAuthToken(): AuthTokenListener {
        return AuthTokenListener { data: Any, _: SocketIOClient ->
            println(data)
            AuthTokenResult.AuthTokenResultSuccess
        }
    }

    fun joinRoom(clientId: UUID, roomId: UUID) {
        namespace.getClient(clientId).joinRoom(roomId.toString())
    }

    fun leaveRoom(clientId: UUID, roomId: UUID) {
        namespace.getClient(clientId).leaveRoom(roomId.toString())
    }

    fun getRoom(roomId: UUID): BroadcastOperations = namespace.getRoomOperations(roomId.toString())

    fun registerDelegate(room: GameRoom) {
        namespace.addEventListener(room.id.toString(), RawData::class.java) { client, data, _ ->
            room.event(client to this, data)
        }
    }

    fun removeDelegate(roomId: UUID) {
        namespace.removeAllListeners(roomId.toString())
    }

    fun <T : RawProtocol> send(operations: ClientOperations, rawData: T) = operations.sendEvent("msg", rawData)

    fun <T : RawProtocol> send(operations: ClientOperations, key: String, rawData: T) = operations.sendEvent(key, rawData)

    fun <T : RawProtocol> sendRoom(roomId: UUID, rawData: T) = send(getRoom(roomId), rawData)

    fun <T : RawProtocol> sendRoom(roomId: UUID, key: String, rawData: T) = send(getRoom(roomId), key, rawData)

    fun <T : RawProtocol> send(room: GameRoom, rawData: T) = sendRoom(room.id, rawData)

    fun <T : RawProtocol> send(room: GameRoom, key: String, rawData: T) = sendRoom(room.id, key, rawData)

    fun <T : RawProtocol> send(clientId: UUID?, rawData: T) = clientId?.let { send(namespace.getClient(clientId), rawData) }

    fun <T : RawProtocol> send(clientId: UUID?, key: String, rawData: T) = clientId?.let { send(namespace.getClient(clientId), key, rawData) }
}
