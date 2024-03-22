package kr.mooner510.konopuro.domain.socket.message

import com.corundumstudio.socketio.BroadcastOperations
import com.corundumstudio.socketio.ClientOperations
import com.corundumstudio.socketio.SocketIONamespace
import kr.mooner510.konopuro.domain.game.component.GameRoom
import kr.mooner510.konopuro.domain.socket.data.RawData
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MessageManager(
    private val namespace: SocketIONamespace
) {
    fun joinRoom(clientId: UUID, roomId: UUID) {
        namespace.getClient(clientId).joinRoom(roomId.toString())
    }

    fun leaveRoom(clientId: UUID, roomId: UUID) {
        namespace.getClient(clientId).leaveRoom(roomId.toString())
    }

    fun getRoom(roomId: UUID): BroadcastOperations = namespace.getRoomOperations(roomId.toString())

    fun <T : RawProtocol> send(operations: ClientOperations, rawData: T) = operations.sendEvent("msg", rawData)

    fun <T : RawProtocol> send(operations: ClientOperations, key: String, rawData: T) = operations.sendEvent(key, rawData)

    fun <T : RawProtocol> sendRoom(roomId: UUID, rawData: T) = send(namespace.getRoomOperations(roomId.toString()), rawData)

    fun <T : RawProtocol> sendRoom(roomId: UUID, key: String, rawData: T) = send(namespace.getRoomOperations(roomId.toString()), key, rawData)

    fun <T : RawProtocol> send(room: GameRoom, rawData: T) = send(room.operations, rawData)

    fun <T : RawProtocol> send(room: GameRoom, key: String, rawData: T) = send(room.operations, key, rawData)

    fun <T : RawProtocol> send(clientId: UUID?, rawData: T) = clientId?.let { send(namespace.getClient(clientId), rawData) }

    fun <T : RawProtocol> send(clientId: UUID?, key: String, rawData: T) = clientId?.let { send(namespace.getClient(clientId), key, rawData) }
}