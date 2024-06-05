package kr.mooner510.konopuro.domain.game.component

import jakarta.transaction.Transactional
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.mooner510.konopuro.domain.game._preset.GamePreset
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.dto.GameCard
import kr.mooner510.konopuro.domain.game.data.card.dto.GameStudentCard
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.socket.data.game.GameRoom
import kr.mooner510.konopuro.domain.socket.data.game.PlayerData
import kr.mooner510.konopuro.domain.game.repository.ActiveDeckRepository
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
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

@Component
class GameManager(
    private val playerCardRepository: PlayerCardRepository,
    private val activeDeckRepository: ActiveDeckRepository,
    private val deckCardRepository: DeckCardRepository,
    private val messageManager: MessageManager,
    private val userRepository: UserRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    companion object {
        private val playerMap: ConcurrentHashMap<UUID, PlayerData> = ConcurrentHashMap()
        private val rooms: ConcurrentHashMap<UUID, GameRoom> = ConcurrentHashMap()
        private val userRoom: ConcurrentHashMap<UUID, UUID> = ConcurrentHashMap()
        private val queue: Queue<UUID> = ConcurrentLinkedQueue()

        fun findRoomByClient(client: UUID): GameRoom {
            return rooms.values.first { value -> value.firstPlayer.client == client || value.secondPlayer.client == client }
        }

        fun findRoomByUser(user: UUID): GameRoom? {
            return rooms.values.firstOrNull { value -> value.firstPlayer.id == user || value.secondPlayer.id == user }
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
    }

    init {
        thread {
            schedule()
        }
    }

    fun matching(user: User) {
        if (user.client == null) throw UserClientNotFoundException()
        queue.offer(user.id)

        for (uuid in queue) {
            logger.info(uuid.toString())
        }
    }

    fun matchingCancel(user: User) {
        if (user.client == null) throw UserClientNotFoundException()
        queue.remove(user.id)
    }

    private fun schedule() = runBlocking {
        logger.info("matching schedule start")
        launch {
            while (true) {
                delay(5000L)
//                logger.info("matching queue ${LocalDateTime.now().withNano(0)}")
                if (queue.size >= 2) {
                    val first = queue.poll()
                    val second = queue.poll()

                    val firstUser = userRepository.findByIdOrNull(first) ?: throw UserNotFoundException()
                    val secondUser = userRepository.findByIdOrNull(second) ?: throw UserNotFoundException()

                    try {
                        val roomId = UUID.randomUUID()

                        if (firstUser.client == null || secondUser.client == null) throw UserClientNotFoundException()

                        logger.info("matching successful: $first with $second to $roomId")
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
        room.map { (player, client) ->
            val deckCards = activeDeckRepository.findByIdOrNull(player)?.let { activeDeck ->
                deckCardRepository.findByDeckId(activeDeck.deckId).mapNotNull { playerCardRepository.findByIdOrNull(it.cardId) }
            } ?: emptyList()

            val students = ArrayList<GameStudentCard>()
            val decks = LinkedList<GameCard>()

            val passiveSet = EnumSet.noneOf(PassiveType::class.java)
            val tierSet = EnumSet.noneOf(TierType::class.java)

            deckCards.forEach {
                if (it.isStudent) {
                    val card = it.toGameStudentCard()
                    students.add(card)
                    passiveSet.addAll(card.passives)
                    tierSet.addAll(card.tiers)
                } else decks.add(it.toGameCard())
            }
            decks.shuffle()

            logger.warn("player $player / client $client")

            val playerData = PlayerData(
                player,
                client,
                students,
                decks,
                ArrayList(),
                0,
                ArrayList(),
                EnumMap(MajorType::class.java),
                EnumMap(MajorType::class.java),
                GamePreset.stage[0],
                false,
                passiveSet,
                tierSet,
            )
            playerMap[player] = playerData
            playerData
        }

        messageManager.registerDelegate(room)
        println("room : $room")
        room.ready(messageManager)
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
