package kr.mooner510.konopuro.domain.socket.data.game

import com.corundumstudio.socketio.SocketIOClient
import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawData
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import kr.mooner510.konopuro.domain.socket.message.MessageManager
import java.util.*

data class GameRoom(
    val id: UUID,
    private val preData: Pair<Pair<UUID, UUID>, Pair<UUID, UUID>>,
    var turn: UUID? = null,
) {
    lateinit var firstPlayer: PlayerData
    lateinit var secondPlayer: PlayerData

    fun forEach(run: (Pair<UUID, UUID>) -> Unit) {
        run(preData.first.first to preData.first.second)
        run(preData.second.first to preData.second.second)
    }

    private fun <T : Any> Pair<SocketIOClient, MessageManager>.other(run: (PlayerData) -> T): T {
        if (firstPlayer.client == this.first.sessionId) return run(secondPlayer)
        return run(firstPlayer)
    }

    private fun <T : Any> Pair<SocketIOClient, MessageManager>.self(run: (PlayerData) -> T): T {
        if (firstPlayer.client == this.first.sessionId) return run(firstPlayer)
        return run(secondPlayer)
    }

    private fun <T : Any> Pair<SocketIOClient, MessageManager>.all(run: (PlayerData) -> T): T {
        if (firstPlayer.client == this.first.sessionId) return run(firstPlayer)
        return run(secondPlayer)
    }

    private fun Pair<SocketIOClient, MessageManager>.self(rawProtocol: RawProtocol) {
        this.second.send(this.self { it.client }, rawProtocol)
    }

    private fun Pair<SocketIOClient, MessageManager>.other(rawProtocol: RawProtocol) {
        this.second.send(this.other { it.client }, rawProtocol)
    }

    private fun Pair<SocketIOClient, MessageManager>.all(rawProtocol: RawProtocol) {
        this.second.sendRoom(id, rawProtocol)
    }

    private fun check(run: (PlayerData) -> Boolean): Boolean {
        return run(firstPlayer) && run(secondPlayer)
    }

    fun event(pairs: Pair<SocketIOClient, MessageManager>, rawData: RawData) {
        when (rawData.protocol) {
            Protocol.Game.Client.GAME_READY -> {

            }

            Protocol.Game.Client.ADD_CARD -> {
            }

            Protocol.Game.Client.SLEEP -> {
                pairs.self {
                    it.isSleep = true
                }
            }
        }
        if (check { it.isSleep || it.time <= 0 }) {
            firstPlayer.time += 24
            secondPlayer.time += 24
//            list.add(RawProtocol(Protocol.Game.Server.NEW_DAY))
//            client.self {
//                it.heldCards.add(it.deckList.removeFirst())
//                list.add(RawProtocol(Protocol.Game.Server.DATA_UPDATE, it))
//            }
//            client.self {
//                it.heldCards.add(it.deckList.removeFirst())
//            }
        }
//        return list
    }
}
