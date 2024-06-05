package kr.mooner510.konopuro.domain.socket.data.game

import com.corundumstudio.socketio.SocketIOClient
import com.fasterxml.jackson.databind.ObjectMapper
import kr.mooner510.konopuro.domain.game._preset.DefaultCardType.*
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawData
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import kr.mooner510.konopuro.domain.socket.message.MessageManager
import kr.mooner510.konopuro.global.utils.UUIDParser
import java.util.*
import kotlin.random.Random

data class GameRoom(
    val id: UUID,
    private val preData: Pair<Pair<UUID, UUID>, Pair<UUID, UUID>>
) {
    private lateinit var turn: UUID

    var date = 0
        private set

    companion object {
        private val objectMapper = ObjectMapper()
    }

    lateinit var firstPlayer: PlayerData
    lateinit var secondPlayer: PlayerData

    fun map(run: (Pair<UUID, UUID>) -> PlayerData) {
        firstPlayer = run(preData.first.first to preData.first.second)
        secondPlayer = run(preData.second.first to preData.second.second)
    }

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

    private fun all(run: (PlayerData) -> Unit) {
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
        if(self().time < other().time)
            turn = self().id
        else if(self().time > other().time)
            turn = other().id

        val modifier = PlayerData.PlayerDataModifier(this@GameRoom, self())
        run(modifier)
        modifier.build()?.let {
            self(Protocol.Game.Server.DATA_UPDATE, RawData(self = it, isTurn = self().id == turn))
            other(Protocol.Game.Server.DATA_UPDATE, RawData(other = it, isTurn = other().id == turn))
        }
    }

    private fun Pair<SocketIOClient, MessageManager>.otherModify(run: (PlayerData.PlayerDataModifier) -> Unit) {
        if(self().time < other().time)
            turn = self().id
        else if(self().time > other().time)
            turn = other().id

        val modifier = PlayerData.PlayerDataModifier(this@GameRoom, other())
        run(modifier)
        modifier.build()?.let {
            self(Protocol.Game.Server.DATA_UPDATE, RawData(other = it, isTurn = self().id == turn))
            other(Protocol.Game.Server.DATA_UPDATE, RawData(self = it, isTurn = other().id == turn))
        }
    }

    private fun Pair<SocketIOClient, MessageManager>.modifyAll(run: (PlayerData.PlayerDataModifier) -> Unit) {
        if(self().time < other().time)
            turn = self().id
        else if(self().time > other().time)
            turn = other().id

        val modifier1 = PlayerData.PlayerDataModifier(this@GameRoom, firstPlayer)
        run(modifier1)
        val build = modifier1.build()

        val modifier2 = PlayerData.PlayerDataModifier(this@GameRoom, secondPlayer)
        run(modifier2)
        val build1 = modifier2.build()

        this.second.sendRoom(firstPlayer.client, RawProtocol(Protocol.Game.Server.DATA_UPDATE, RawData(build, build1, firstPlayer.id == turn)))
        this.second.sendRoom(secondPlayer.client, RawProtocol(Protocol.Game.Server.DATA_UPDATE, RawData(build1, build, secondPlayer.id == turn)))
    }

    fun ready(manager: MessageManager) {
        val run: (PlayerData.PlayerDataModifier) -> Unit = { modifier ->
            repeat(5) { modifier.pickupDeck() }
            modifier.applyStudentData()
        }

        turn = firstPlayer.id

        val modifier1 = PlayerData.PlayerDataModifier(this@GameRoom, firstPlayer)
        run(modifier1)
        val build = modifier1.build()

        val modifier2 = PlayerData.PlayerDataModifier(this@GameRoom, secondPlayer)
        run(modifier2)
        val build1 = modifier2.build()

        manager.send(firstPlayer.client, RawProtocol(Protocol.Game.Server.GAME_START, RawData(build, build1, firstPlayer.id == turn)))
        manager.send(secondPlayer.client, RawProtocol(Protocol.Game.Server.GAME_START, RawData(build1, build, secondPlayer.id == turn)))
    }

    fun event(pairs: Pair<SocketIOClient, MessageManager>, rawProtocol: RawProtocol) {
        when (rawProtocol.protocol) {
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
            }

            Protocol.Game.Client.USE_CARD -> {
                println("cardUse")
                println(rawProtocol.data[0])
                val uuid = UUIDParser.transfer(rawProtocol[0].toString())
                pairs.selfModify {
                    val card = it.useCard(uuid)
                    pairs.other(Protocol.Game.Server.SUCCESS_CARD, card)
                }
            }

            Protocol.Game.Client.USE_ABILITY -> {
                val tierType = TierType.valueOf(rawProtocol[0].toString())
                pairs.selfModify {
                    val tier = it.useAbility(tierType) ?: return@selfModify
                    pairs.other(Protocol.Game.Server.SUCCESS_ABILITY, tier, it.activeStudent)
                }
            }
        }

        if (pairs.checkAll { it.isSleep || it.time <= 0 }) {
            date++
            pairs.modifyAll {
                it.addTime(24)
                MajorType.entries.forEach { major ->
                    it.remove(major.dataKey)
                }
                it.newDay(date)
            }
            pairs.all(Protocol.Game.Server.NEW_DAY)
        }
    }
}
