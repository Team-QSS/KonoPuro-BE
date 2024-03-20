package kr.mooner510.konopuro.domain.socket.game

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Queue
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class GameManager(
) {
    private val rooms: MutableMap<UUID, GameRoom> = ConcurrentHashMap()
    private val userRoom: MutableMap<UUID, UUID> = ConcurrentHashMap()
    private val queue: Queue<UUID> = ConcurrentLinkedQueue()

    init {
        schedule()
    }

    fun matching(user: User) {
        queue.offer(user.id)
    }

    fun getRoom(id: UUID): GameRoom? {
        return rooms[id]
    }

    fun getRoomByPlayer(userId: UUID): GameRoom? {
        return userRoom[userId]?.let { rooms[it] }
    }

    private fun schedule() = runBlocking {
        println("matching schedule start")
        launch {
            while (true) {
                delay(5000L)
                println("matching queue ${LocalDateTime.now()}")
                if (queue.size >= 2) {
                    val first = queue.poll()
                    val second = queue.poll()
                    val roomId = UUID.randomUUID()
                    println("matching successful: $first with $second to $roomId")
                    rooms[roomId] = GameRoom(roomId, first, second)
                    userRoom[first] = roomId
                    userRoom[second] = roomId
//                    template.convertAndSend("/subscribe/match/$first", roomId)
//                    template.convertAndSend("/subscribe/match/$second", roomId)
                }
            }
        }
    }
}