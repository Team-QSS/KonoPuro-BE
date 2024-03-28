package kr.mooner510.konopuro.domain.game.component

import com.corundumstudio.socketio.BroadcastOperations
import java.util.*

data class GameRoom(
    val id: UUID,
    val firstUser: UUID,
    val secondUser: UUID,
    val firstClient: UUID,
    val secondClient: UUID,
    val operations: BroadcastOperations,
    val nextTurn: UUID?
) {
    fun forEach(run: (Pair<UUID, UUID>) -> Unit) {
        run(firstUser to firstClient)
        run(secondUser to secondClient)
    }
}
