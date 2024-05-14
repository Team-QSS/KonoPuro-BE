package kr.mooner510.konopuro.domain.socket.data.game

import com.corundumstudio.socketio.SocketIOClient
import com.fasterxml.jackson.databind.ObjectMapper
import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawData
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import kr.mooner510.konopuro.domain.socket.message.MessageManager
import kr.mooner510.konopuro.global.utils.UUIDParser
import java.util.*
import kotlin.collections.ArrayList

data class GameRoom(
    val id: UUID,
    private val preData: Pair<Pair<UUID, UUID>, Pair<UUID, UUID>>,
    var turn: UUID? = null,
) {
    private val todayLog = ArrayList<GameLog>()

    companion object {
        private val objectMapper = ObjectMapper()
    }

    lateinit var firstPlayer: PlayerData
    lateinit var secondPlayer: PlayerData

    fun forEach(run: (Pair<UUID, UUID>) -> Unit) {
        run(preData.first.first to preData.first.second)
        run(preData.second.first to preData.second.second)
    }

    private fun Pair<SocketIOClient, MessageManager>.other(): PlayerData {
        if (firstPlayer.client == this.first.sessionId) return secondPlayer
        return firstPlayer
    }

    private fun Pair<SocketIOClient, MessageManager>.self(): PlayerData {
        if (firstPlayer.client == this.first.sessionId) return firstPlayer
        return secondPlayer
    }

    private fun <T : Any> Pair<SocketIOClient, MessageManager>.other(run: (PlayerData) -> T): T {
        return run(this.other())
    }

    private fun <T : Any> Pair<SocketIOClient, MessageManager>.self(run: (PlayerData) -> T): T {
        return run(this.self())
    }

    private fun Pair<SocketIOClient, MessageManager>.all(run: (PlayerData) -> Unit) {
        run(firstPlayer)
        run(secondPlayer)
    }

    private fun Pair<SocketIOClient, MessageManager>.self(protocol: Int, vararg data: Any) {
        this.second.send(this.self { it.client }, RawProtocol(protocol, *data))
    }

    private fun Pair<SocketIOClient, MessageManager>.other(protocol: Int, vararg data: Any) {
        this.second.send(this.other { it.client }, RawProtocol(protocol, *data))
    }

    private fun Pair<SocketIOClient, MessageManager>.all(protocol: Int, vararg data: Any) {
        this.second.send(this@GameRoom, RawProtocol(protocol, *data))
    }

    private fun Pair<SocketIOClient, MessageManager>.selfCheck(run: (PlayerData) -> Boolean): Boolean {
        return run(self())
    }

    private fun Pair<SocketIOClient, MessageManager>.otherCheck(run: (PlayerData) -> Boolean): Boolean {
        return run(other())
    }

    private fun Pair<SocketIOClient, MessageManager>.checkAll(run: (PlayerData) -> Boolean): Boolean {
        return run(firstPlayer) && run(secondPlayer)
    }

    private fun <T> String.parse(clazz: Class<T>): T {
        return objectMapper.readValue(this, clazz)
    }

    private fun Pair<SocketIOClient, MessageManager>.selfModify(run: (PlayerData.PlayerDataModifier) -> Unit) {
        val modifier = PlayerData.PlayerDataModifier(todayLog, self())
        run(modifier)
        self(Protocol.Game.Server.DATA_UPDATE, modifier.build())
    }

    private fun Pair<SocketIOClient, MessageManager>.otherModify(run: (PlayerData.PlayerDataModifier) -> Unit) {
        val modifier = PlayerData.PlayerDataModifier(todayLog, other())
        run(modifier)
        other(Protocol.Game.Server.DATA_UPDATE, modifier.build())
    }

    private fun Pair<SocketIOClient, MessageManager>.modifyAll(run: (PlayerData.PlayerDataModifier) -> Unit) {
        val modifier1 = PlayerData.PlayerDataModifier(todayLog, firstPlayer)
        run(modifier1)
        this.second.send(firstPlayer.client, modifier1.build())

        val modifier2 = PlayerData.PlayerDataModifier(todayLog, secondPlayer)
        run(modifier2)
        this.second.send(secondPlayer.client, modifier2.build())
    }

    fun event(pairs: Pair<SocketIOClient, MessageManager>, rawData: RawData) {
        when (rawData.protocol) {
            Protocol.Game.Client.GAME_READY -> {

            }

            Protocol.Game.Client.ADD_CARD -> {
                pairs.selfModify {
                    pairs.self(Protocol.Game.Server.NEW_CARD, it.pickupDeck())
                    pairs.other(Protocol.Game.Client.ADD_CARD_OTHER)
                }
            }

            Protocol.Game.Client.SLEEP -> {
                pairs.selfModify {
                    it.sleep()
                    pairs.other(Protocol.Game.Server.OTHER_SLEEP)
                }

                if (pairs.checkAll { it.isSleep || it.time <= 0 }) {
                    pairs.modifyAll { it.addTime(24) }
                    pairs.all(Protocol.Game.Server.NEW_DAY)
                    todayLog.clear()
                }
            }

            Protocol.Game.Client.USE_CARD -> {
                val uuid = UUIDParser.transfer(rawData[0])
                pairs.selfModify {
                    val card = it.useCard(uuid)
                    when (card.defaultCardType) {
                        else -> {}
                    }
                    pairs.other(Protocol.Game.Server.SUCCESS_CARD, card)
                }
            }

            Protocol.Game.Client.USE_ABILITY -> {
                pairs.selfModify {
                    val card = it.
                }
            }
        }
    }
}
