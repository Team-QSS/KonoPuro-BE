package kr.mooner510.konopuro.domain.game.component

import jakarta.transaction.Transactional
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.mooner510.konopuro.domain.game.data.card.dto.GameCard
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game._preset.GamePreset
import kr.mooner510.konopuro.domain.socket.data.game.GameRoom
import kr.mooner510.konopuro.domain.socket.data.game.PlayerData
import kr.mooner510.konopuro.domain.game.repository.ActiveDeckRepository
import kr.mooner510.konopuro.domain.game.repository.CardDataRepository
import kr.mooner510.konopuro.domain.game.repository.DeckCardRepository
import kr.mooner510.konopuro.domain.game.repository.PlayerCardRepository
import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import kr.mooner510.konopuro.domain.socket.exception.RoomNotFoundException
import kr.mooner510.konopuro.domain.socket.exception.UserClientNotFoundException
import kr.mooner510.konopuro.domain.socket.message.MessageManager
import kr.mooner510.konopuro.global.global.error.data.GlobalError
import kr.mooner510.konopuro.global.security.data.entity.User
import kr.mooner510.konopuro.global.security.exception.UserNotFoundException
import kr.mooner510.konopuro.global.security.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

@Component
class GameManager(
    private val playerCardRepository: PlayerCardRepository,
    private val cardDataRepository: CardDataRepository,
    private val activeDeckRepository: ActiveDeckRepository,
    private val deckCardRepository: DeckCardRepository,
    private val messageManager: MessageManager,
    private val userRepository: UserRepository,
) {
    companion object {
        private val playerMap: ConcurrentHashMap<UUID, PlayerData> = ConcurrentHashMap()
        private val rooms: ConcurrentHashMap<UUID, GameRoom> = ConcurrentHashMap()
        private val userRoom: ConcurrentHashMap<UUID, UUID> = ConcurrentHashMap()
        private val queue: Queue<UUID> = ConcurrentLinkedQueue()
    }

    init {
        thread {
            schedule()
        }
    }

    fun matching(user: User) {
        queue.offer(user.id)
    }

    fun getRoom(id: UUID): GameRoom {
        return rooms[id] ?: throw RoomNotFoundException()
    }

    fun getRoomByPlayer(userId: UUID): GameRoom {
        return userRoom[userId]?.let { rooms[it] } ?: throw RoomNotFoundException()
    }

    fun removeRoom(id: UUID): GameRoom? {
        return rooms.remove(id)
    }

    private fun schedule() = runBlocking {
        println("matching schedule start")
        launch {
            while (true) {
                delay(5000L)
//                println("matching queue ${LocalDateTime.now().withNano(0)}")
                if (queue.size >= 2) {
                    val first = queue.poll()
                    val second = queue.poll()

                    val firstUser = userRepository.findByIdOrNull(first) ?: throw UserNotFoundException()
                    val secondUser = userRepository.findByIdOrNull(second) ?: throw UserNotFoundException()

                    try {
                        val roomId = UUID.randomUUID()

                        if (firstUser.client == null || secondUser.client == null) throw UserClientNotFoundException()

                        println("matching successful: $first with $second to $roomId")
                        messageManager.joinRoom(firstUser.client!!, roomId)
                        messageManager.joinRoom(secondUser.client!!, roomId)

                        val gameRoom = GameRoom(
                            roomId,
                            (firstUser.id to firstUser.client!!) to (secondUser.id to secondUser.client!!)
                        )
                        rooms[roomId] = gameRoom
                        userRoom[first] = roomId
                        userRoom[second] = roomId

                        startGame(gameRoom)
                    } catch (e: GlobalError) {
                        messageManager.send(firstUser.client, RawProtocol(Protocol.Match.ROOM_MATCH_FAILED, e.message!!))
                    }
                }
            }
        }
    }

    @Transactional
    fun reconnect(userId: UUID, clientId: UUID) {
        val room = getRoomByPlayer(userId)
        if (room.firstPlayer.id == userId) {
            messageManager.leaveRoom(room.firstPlayer.client, room.id)
            room.firstPlayer.client = clientId
            userRepository.findByIdOrNull(userId)?.let { it.client = clientId }
            messageManager.joinRoom(clientId, room.id)
        } else {
            messageManager.leaveRoom(room.secondPlayer.client, room.id)
            room.secondPlayer.client = clientId
            userRepository.findByIdOrNull(userId)?.let { it.client = clientId }
            messageManager.joinRoom(clientId, room.id)
        }
    }

    fun startGame(room: GameRoom) {
        room.forEach { (player, client) ->
            val deckCards = activeDeckRepository.findByIdOrNull(player)?.let { activeDeck ->
                deckCardRepository.findByDeckId(activeDeck.deckId).mapNotNull {
                    val playerCard = playerCardRepository.findByIdOrNull(it.cardId) ?: return@mapNotNull null
                    val cardData = cardDataRepository.findByIdOrNull(playerCard.cardDataId) ?: return@mapNotNull null
                    GameCard.new(playerCard, cardData)
                }
            } ?: emptyList()

            playerMap[player] = PlayerData(
                player,
                client,
                deckCards.filter { it.type == CardType.Student }.toMutableList(),
                LinkedList(deckCards.filter { it.type != CardType.Student }),
                mutableListOf(),
                0,
                mutableListOf(),
                mutableMapOf(),
                mutableMapOf(),
                GamePreset.stage[0],
                false
            )
        }

        messageManager.send(room, RawProtocol(Protocol.Match.ROOM_MATCHED, room.id))
    }

    fun endGame(room: GameRoom) {
        room.forEach { (player, client) ->
            messageManager.leaveRoom(room.id, client)
            playerMap.remove(player)
        }
        removeRoom(room.id)
    }
}