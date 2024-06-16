package kr.mooner510.konopuro.domain.socket.data.game

import com.corundumstudio.socketio.SocketIOClient
import com.fasterxml.jackson.databind.ObjectMapper
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.component.GameManager
import kr.mooner510.konopuro.domain.socket.data.*
import kr.mooner510.konopuro.domain.socket.message.MessageManager
import kr.mooner510.konopuro.global.utils.UUIDParser
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

data class GameRoom(
    val id: UUID,
    private val preData: Pair<Pair<UUID, UUID>, Pair<UUID, UUID>>
) {
    lateinit var turn: UUID
    var nextTurn: UUID? = null

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

    private fun Pair<SocketIOClient, MessageManager>.selfCheck(run: (PlayerData) -> Boolean): Boolean {
        return run(self())
    }

    private fun Pair<SocketIOClient, MessageManager>.otherCheck(run: (PlayerData) -> Boolean): Boolean {
        return run(other())
    }

    private fun Pair<SocketIOClient, MessageManager>.checkAll(run: (PlayerData) -> Boolean): Boolean {
        return run(firstPlayer) && run(secondPlayer)
    }

    private fun Pair<SocketIOClient, MessageManager>.checkIf(run: (PlayerData) -> Boolean): List<PlayerData> {
        val list = ArrayList<PlayerData>(2)
        if (run(firstPlayer)) list.add(firstPlayer)
        if (run(secondPlayer)) list.add(secondPlayer)
        return list
    }

    fun ready(manager: MessageManager) {
        val run: (PlayerData.PlayerDataModifier) -> Unit = { modifier ->
            repeat(5) { modifier.pickupDeck() }
            modifier.applyStudentData()
        }

        turn = if (Random.nextFloat() < 0.5f) firstPlayer.id else secondPlayer.id

        val modifier1 = PlayerData.PlayerDataModifier(this@GameRoom, firstPlayer)
        run(modifier1)
        val build = modifier1.build()

        val modifier2 = PlayerData.PlayerDataModifier(this@GameRoom, secondPlayer)
        run(modifier2)
        val build1 = modifier2.build()

        manager.send(firstPlayer.client, RawProtocol(Protocol.Game.Server.GAME_START, RawData(build, build1, firstPlayer.id == turn)).toList())
        manager.send(secondPlayer.client, RawProtocol(Protocol.Game.Server.GAME_START, RawData(build1, build, secondPlayer.id == turn)).toList())
    }

    fun Pair<SocketIOClient, MessageManager>.send(run: Sender.() -> Unit) {
        val firstList = ArrayList<RawProtocol>()
        val secondList = ArrayList<RawProtocol>()
        val sender = Sender(this@GameRoom, this, firstList, secondList)
        run(sender)
        this.second.send(self().client, RawProtocols(firstList))
        this.second.send(other().client, RawProtocols(secondList))
    }

    fun event(pairs: Pair<SocketIOClient, MessageManager>, rawProtocol: RawProtocol): Unit = pairs.send {
        when (rawProtocol.protocol) {
//            Protocol.Game.Client.ADD_CARD -> {
//                pairs.selfModify {
//                    pairs.self(Protocol.Game.Server.NEW_CARD, it.pickupDeck())
//                    pairs.other(Protocol.Game.Client.ADD_CARD_OTHER)
//                }
//            }

            Protocol.Game.Client.SLEEP -> {
                if (nextTurn == null) nextTurn = pairs.self().id
                selfModify {
                    it.sleep()
                }
            }

            Protocol.Game.Client.USE_CARD -> {
                println("cardUse")
                println(rawProtocol.data[0])
                val uuid = UUIDParser.transfer(rawProtocol[0].toString())
                selfModify {
                    val card = it.useCard(uuid)
                    other(Protocol.Game.Server.SUCCESS_CARD, card!!)
                }
            }

            Protocol.Game.Client.USE_ABILITY -> {
                val useCardId = rawProtocol[0].toString()
                val tierType = TierType.valueOf(rawProtocol[1].toString())
                selfModify {
                    val tier = it.useAbility(tierType) ?: return@selfModify
                    other(Protocol.Game.Server.SUCCESS_ABILITY, tier, it.activeStudent)
                }
            }
        }

        val checkList = pairs.checkIf { data ->
            data.goal.all { data.project.getOrDefault(it.key, 0) >= it.value }
        }

        if (checkList.isNotEmpty()) {
            if (checkList.size == 1) {
                all(Protocol.Game.Server.GAME_END, checkList[0].id)
                GameManager.instance.endGame(this@GameRoom)
                return@send
            }
            all(Protocol.Game.Server.GAME_END, "DRAW")
            GameManager.instance.endGame(this@GameRoom)
            return@send
        }

        if (nextTurn == null && pairs.self().time <= 0) {
            nextTurn = pairs.self().id
        }
        if (nextTurn == null && pairs.other().time <= 0) {
            nextTurn = pairs.other().id
        }

        if (pairs.checkAll { it.isSleep || it.time <= 0 }) {
            date++
            turn = nextTurn!!
            nextTurn = null
            all(Protocol.Game.Server.NEW_DAY)
            modifyAll {
                it.newDay(date)
            }
        }
    }
}
