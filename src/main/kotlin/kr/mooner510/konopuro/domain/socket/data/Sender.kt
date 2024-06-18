package kr.mooner510.konopuro.domain.socket.data

import com.corundumstudio.socketio.SocketIOClient
import kr.mooner510.konopuro.domain.socket.data.game.GameRoom
import kr.mooner510.konopuro.domain.socket.data.game.PlayerData
import kr.mooner510.konopuro.domain.socket.message.MessageManager

class Sender(
    private val gameRoom: GameRoom,
    private val pair: Pair<SocketIOClient, MessageManager>,
    private val firstList: ArrayList<RawProtocol>,
    private val secondList: ArrayList<RawProtocol>
) {

    private fun Pair<SocketIOClient, MessageManager>.calcTurn() {
        val self = self()
        val other = other()

        gameRoom.turn = if (!self.isSleep && self.time > 0) {
            if (!other.isSleep && other.time > 0) {
                if (other.time > self.time) other.id
                else if (other.time < self.time) self.id
                else if (gameRoom.turn == self.id) other.id else self.id
            } else {
                self.id
            }
        } else if (!other.isSleep && other.time > 0) {
            other.id
        } else {
            gameRoom.nextTurn ?: gameRoom.turn
        }
    }

    private fun Pair<SocketIOClient, MessageManager>.other(): PlayerData {
        if (gameRoom.firstPlayer.client == this.first.sessionId) return gameRoom.secondPlayer
        return gameRoom.firstPlayer
    }

    private fun Pair<SocketIOClient, MessageManager>.self(): PlayerData {
        if (gameRoom.firstPlayer.client == this.first.sessionId) return gameRoom.firstPlayer
        return gameRoom.secondPlayer
    }

    fun <T : Any> Pair<SocketIOClient, MessageManager>.other(run: (PlayerData) -> T): T {
        return run(this.other())
    }

    fun <T : Any> Pair<SocketIOClient, MessageManager>.self(run: (PlayerData) -> T): T {
        return run(this.self())
    }

    fun modify(run: PlayerData.PlayerDataModifierGroup.() -> Unit) {
        val modifier = PlayerData.PlayerDataModifierGroup(gameRoom, pair.self(), pair.other())
        run(modifier)
        val (self, other) = modifier.build()
        pair.calcTurn()
        firstList.add(RawProtocol(Protocol.Game.Server.DATA_UPDATE, RawData(self = self, other = other, isTurn = pair.self().id == gameRoom.turn)))
        secondList.add(RawProtocol(Protocol.Game.Server.DATA_UPDATE, RawData(self = other, other = self, isTurn = pair.other().id == gameRoom.turn)))
    }

    fun all(protocol: Int, vararg data: Any) {
        firstList.add(RawProtocol(protocol, *data))
        secondList.add(RawProtocol(protocol, *data))
    }

    fun self(protocol: Int, vararg data: Any) {
        firstList.add(RawProtocol(protocol, *data))
    }

    fun other(protocol: Int, vararg data: Any) {
        secondList.add(RawProtocol(protocol, *data))
    }
}
