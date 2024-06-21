package kr.mooner510.konopuro.domain.socket.message

import com.corundumstudio.socketio.*
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.listener.DisconnectListener
import com.fasterxml.jackson.databind.ObjectMapper
import kr.mooner510.konopuro.domain.game.component.GameManager
import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import kr.mooner510.konopuro.domain.socket.data.RawProtocols
import kr.mooner510.konopuro.domain.socket.data.game.GameRoom
import kr.mooner510.konopuro.global.security.component.TokenProvider
import kr.mooner510.konopuro.global.security.repository.UserRepository
import kr.mooner510.konopuro.global.utils.UUIDParser
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun nameSpace(): SocketIONamespace {
        return namespace
    }

    init {
        val objectMapper = ObjectMapper()
        namespace.let {
            it.addConnectListener(onConnected())
            it.addDisconnectListener(onDisconnected())
            it.addAuthTokenListener(onAuthToken())
            it.addEventInterceptor { client, eventName, args, _ ->
                logger.info("Client[${client.sessionId}] - Received chat message on ($eventName) '${objectMapper.writeValueAsString(args)}'")
            }
            server.addEventInterceptor { namespaceClient, s, _, _ ->
                logger.info("Event Listen($s): ${namespaceClient.sessionId}")
            }
            it.addEventListener("chat", RawProtocol::class.java, onChatReceived())
        }
    }


    private fun onConnected(): ConnectListener {
        return ConnectListener { client: SocketIOClient ->
            val authorization = client.handshakeData.httpHeaders.get("Authorization", "")
            if (authorization.isBlank()) {
                logger.info("Auth: $authorization")
                client.disconnect()
                return@ConnectListener
            }
            val handshakeData = client.handshakeData
            logger.info("Client[${client.sessionId}] - Connected to chat module through '${handshakeData.url}' / Authorization: $authorization with session ${client.sessionId}")
            val authKey = tokenProvider.getAccessKey(UUIDParser.transfer(authorization))
            userRepository.updateClientById(client.sessionId, authKey.userId)

            GameManager.findRoomByUser(authKey.userId)?.let { room ->
                if (room.firstPlayer.id == authKey.userId) {
                    leaveRoom(room.firstPlayer.client, room.id)
                    room.firstPlayer.client = client.sessionId
                    joinRoom(room.firstPlayer.client, room.id)
                } else {
                    leaveRoom(room.secondPlayer.client, room.id)
                    room.secondPlayer.client = client.sessionId
                    joinRoom(room.secondPlayer.client, room.id)
                }
                send(room, RawProtocol(Protocol.Match.RECONNECTED).toList())
            }
        }
    }

    private fun onDisconnected(): DisconnectListener {
        return DisconnectListener { client: SocketIOClient ->
            val authorization = client.handshakeData.httpHeaders.get("Authorization", "")
            if (authorization.isBlank()) return@DisconnectListener
            logger.info("Client[${client.sessionId}] - Disconnected from chat module.")
            userRepository.updateClientToNull(client.sessionId)
            GameManager.findRoomByClient(client.sessionId)?.let {
                send(it, RawProtocol(Protocol.Match.DISCONNECTED).toList())
            }
        }
    }

    private fun onChatReceived(): DataListener<RawProtocol> {
        return DataListener<RawProtocol> { client: SocketIOClient, data: RawProtocol, _: AckRequest? ->
            logger.info("Client[${client.sessionId}] - Received chat message '${data}'")
            namespace.broadcastOperations.sendEvent("chat", data)
        }
    }

    private fun onAuthToken(): AuthTokenListener {
        return AuthTokenListener { data: Any, _: SocketIOClient ->
            logger.info(data.toString())
            AuthTokenResult.AuthTokenResultSuccess
        }
    }

    fun joinRoom(clientId: UUID, roomId: UUID) {
        namespace.getClient(clientId)?.joinRoom(roomId.toString())
    }

    fun leaveRoom(clientId: UUID, roomId: UUID) {
        namespace.getClient(clientId)?.leaveRoom(roomId.toString())
    }

    fun getRoom(roomId: UUID): BroadcastOperations = namespace.getRoomOperations(roomId.toString())

    fun registerDelegate(room: GameRoom) {
        namespace.addEventListener(room.id.toString(), RawProtocol::class.java) { client, data, _ ->
            room.event(client to this, data)
        }
    }

    fun removeDelegate(room: GameRoom) {
        namespace.removeAllListeners(room.id.toString())
    }

    fun <T : RawProtocols> send(operations: ClientOperations, rawData: T) = operations.sendEvent("msg", rawData)

    fun <T : RawProtocols> send(operations: ClientOperations, key: String, rawData: T) = operations.sendEvent(key, rawData)

    fun <T : RawProtocols> sendRoom(roomId: UUID, rawData: T) = send(getRoom(roomId), rawData)

    fun <T : RawProtocols> sendRoom(roomId: UUID, key: String, rawData: T) = send(getRoom(roomId), key, rawData)

    fun <T : RawProtocols> send(room: GameRoom, rawData: T) = sendRoom(room.id, rawData)

    fun <T : RawProtocols> send(room: GameRoom, key: String, rawData: T) = sendRoom(room.id, key, rawData)

    fun <T : RawProtocols> send(clientId: UUID?, rawData: T) = clientId?.let { send(namespace.getClient(clientId), rawData) }

    fun <T : RawProtocols> send(clientId: UUID?, key: String, rawData: T) = clientId?.let { send(namespace.getClient(clientId), key, rawData) }
}
