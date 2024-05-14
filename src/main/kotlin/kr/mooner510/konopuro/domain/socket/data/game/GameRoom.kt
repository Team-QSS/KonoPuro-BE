package kr.mooner510.konopuro.domain.socket.data.game

import com.corundumstudio.socketio.SocketIOClient
import com.fasterxml.jackson.databind.ObjectMapper
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawData
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import kr.mooner510.konopuro.domain.socket.message.MessageManager
import kr.mooner510.konopuro.global.utils.UUIDParser
import java.util.*

data class GameRoom(
    val id: UUID,
    private val preData: Pair<Pair<UUID, UUID>, Pair<UUID, UUID>>,
    var turn: UUID? = null,
) {
    companion object {
        private val objectMapper = ObjectMapper()
    }

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

    private fun Pair<SocketIOClient, MessageManager>.self(protocol: Int, vararg data: Any) {
        this.second.send(this.self { it.client }, RawProtocol(protocol, *data))
    }

    private fun Pair<SocketIOClient, MessageManager>.other(protocol: Int, vararg data: Any) {
        this.second.send(this.other { it.client }, RawProtocol(protocol, *data))
    }

    private fun Pair<SocketIOClient, MessageManager>.all(protocol: Int, vararg data: Any) {
        this.second.sendRoom(id, RawProtocol(protocol, *data))
    }

    private fun check(run: (PlayerData) -> Boolean): Boolean {
        return run(firstPlayer) && run(secondPlayer)
    }

    private fun <T> String.parse(clazz: Class<T>): T {
        return objectMapper.readValue(this, clazz)
    }

    fun event(pairs: Pair<SocketIOClient, MessageManager>, rawData: RawData) {
        when (rawData.protocol) {
            Protocol.Game.Client.GAME_READY -> {

            }

            Protocol.Game.Client.ADD_CARD -> {
                pairs.self {
                    val card = it.deckList.removeFirst()
                    it.heldCards.add(card)
                    pairs.self(Protocol.Game.Server.NEW_CARD, card)
                    pairs.other(Protocol.Game.Server.NEW_CARD)
                }
                pairs.other(Protocol.Game.Client.ADD_CARD_OTHER)
            }

            Protocol.Game.Client.SLEEP -> {
                pairs.self { it.isSleep = true }
                pairs.other(Protocol.Game.Server.SLEEP)
            }

            Protocol.Game.Client.USE_CARD -> {
                val uuid = UUIDParser.transfer(rawData[0])
                pairs.self { playerData ->
                    val card = playerData.heldCards.first { it.playerCardId == uuid }
                    pairs.other(Protocol.Game.Server.SUCCESS_CARD, card)
                }
            }

            Protocol.Game.Client.USE_ABILITY -> {
//                val tierType = TierType.valueOf(rawData[0])
                pairs.self {
                    pairs.other(Protocol.Game.Server.SUCCESS_ABILITY, rawData[0])
                }
            }
        }
        if (check { it.isSleep || it.time <= 0 }) {
            firstPlayer.time += 24
            secondPlayer.time += 24
            pairs.other(Protocol.Game.Server.NEW_DAY)
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
