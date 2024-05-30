package kr.mooner510.konopuro.domain.socket.data.game

import com.fasterxml.jackson.databind.ObjectMapper
import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.dto.GameCard
import kr.mooner510.konopuro.domain.game.data.card.dto.GameStudentCard
import kr.mooner510.konopuro.domain.game.data.card.manager.CardManager.useTier
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import kr.mooner510.konopuro.domain.socket.data.game.PlayerData.Modifier.*
import kr.mooner510.konopuro.domain.socket.data.obj.GameCards
import kr.mooner510.konopuro.domain.socket.data.obj.GameStudentCards
import kr.mooner510.konopuro.domain.socket.data.type.DataKey
import kr.mooner510.konopuro.global.utils.UUIDParser
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class PlayerData(
    val id: UUID,
    var client: UUID,
    val students: ArrayList<GameStudentCard>,
    val deckList: LinkedList<GameCard>,
    val heldCards: ArrayList<GameCard>,
    var time: Int,
    val fieldCards: ArrayList<GameCard>,
    val project: EnumMap<MajorType, Int>,
    val issue: EnumMap<MajorType, LinkedList<Int>>,
    val goal: Map<MajorType, Int>,
    var isSleep: Boolean,
    val passives: EnumSet<PassiveType>,
    val tiers: EnumSet<TierType>,
) {
    private val dataIntMap = EnumMap<DataKey, Int>(DataKey::class.java)
    private val dataDoubleMap = EnumMap<DataKey, Double>(DataKey::class.java)

    enum class Modifier {
        Client,
        Student,
        Deck,
        HeldCard,
        Time,
        FieldCard,
        Project,
        Issue,
        Sleep,
        DataInt,
        DataDouble,
        Students
    }

    class PlayerDataModifier(
        private val gameRoom: GameRoom,
        private val playerData: PlayerData
    ) {
        companion object {
            private val objectMapper = ObjectMapper()
        }

        private val modifiers = EnumSet.noneOf(Modifier::class.java)
        lateinit var activeStudent: GameStudentCard
        private var modifiedStudent: HashMap<UUID, GameStudentCard.GameStudentCardModifier> = hashMapOf()

        fun <T> execute(run: PlayerData.() -> T): T {
            return run(playerData)
        }

        fun modifyStudents(run: (GameStudentCard.GameStudentCardModifier) -> Unit) {
            modifiers.add(Students)
            playerData.students.forEach {
                val modifier = GameStudentCard.GameStudentCardModifier(gameRoom.date, it)
                run(modifier)
                modifiedStudent[it.id] = modifier
            }
        }

        fun modifyStudent(id: UUID, run: (GameStudentCard.GameStudentCardModifier) -> Unit) {
            run(modifiedStudent.getOrPut(id) { GameStudentCard.GameStudentCardModifier(gameRoom.date, playerData.students.find { it.id == id }!!) })
        }

        fun modifyStudent(studentCard: GameStudentCard, run: (GameStudentCard.GameStudentCardModifier) -> Unit) {
            run(modifiedStudent.getOrPut(studentCard.id) { GameStudentCard.GameStudentCardModifier(gameRoom.date, studentCard) })
        }

        fun newDay(date: Int) = execute {
            modifyStudents { it.removeIfEndDate(date) }
            fieldCards.mapNotNull { if (it.dayTime) it.defaultCardType else null }.toSet().forEach {
                removeFieldCardLimit(it)
            }
        }

        fun setClient(uuid: UUID) = execute {
            client = uuid
            modifiers.add(Client)
        }

        fun pickupDeck(): GameCard = execute {
            val card = deckList.removeFirst()
            heldCards.add(card)
            modifiers.add(Deck)
            modifiers.add(HeldCard)
            return@execute card
        }

        fun addTime(time: Int) = execute {
            if (time <= 0) return@execute
            this.time += time
            modifiers.add(Time)
        }

        fun removeTime(time: Int) = execute {
            if (this.time <= 0) return@execute false
            this.time -= time
            modifiers.add(Time)
            return@execute true
        }

        fun removeTime(time: Int, after: () -> Unit) = execute {
            if (this.time <= 0) return@execute false
            this.time -= time
            modifiers.add(Time)
            after()
            return@execute true
        }

        fun sleep() = execute {
            if (isSleep) return@execute
            isSleep = true
            modifiers.add(Sleep)
        }

        fun addProject(majorType: MajorType, value: Int) = execute {
            val issueList = issue.getOrElse(majorType) { LinkedList() }
            var afterValue = value
            var next: Int
            if (issueList.isNotEmpty()) {
                val iterator = issueList.listIterator()
                while (iterator.hasNext()) {
                    next = iterator.next()
                    if (next > afterValue) {
                        iterator.set(next - afterValue)
                        afterValue = 0
                        break
                    } else {
                        afterValue -= next
                        iterator.remove()
                    }
                }
            }
            addInt(majorType.dataTotalKey, value)
            addInt(majorType.dataKey, value)
            project.merge(majorType, afterValue, Integer::sum)
            modifiers.add(Project)
        }

        fun addFieldCard(defaultCardType: DefaultCardType, limit: Int, dupe: Boolean = false, dayTime: Boolean = false) = execute {
            if (dupe) fieldCards.add(GameCard(UUIDParser.nilUUID, defaultCardType, limit, dayTime))
            else {
                fieldCards.find { it.defaultCardType == defaultCardType }?.let {
                    it.limit = limit
                    return@execute
                }
                fieldCards.add(GameCard(UUIDParser.nilUUID, defaultCardType, limit, dayTime))
            }
        }

        fun removeFieldCard(defaultCardType: DefaultCardType) = execute {
            fieldCards.removeIf { it.defaultCardType == defaultCardType }
            modifiers.add(FieldCard)
        }

        fun removeFieldCardLimit(defaultCardType: DefaultCardType, limit: Int = 1, multi: Boolean = true): Boolean = execute {
            val iterator = fieldCards.listIterator()
            var next: GameCard
            if (multi) {
                var done = false
                while (iterator.hasNext()) {
                    next = iterator.next()
                    if (next.defaultCardType == defaultCardType) {
                        done = true
                        next.limit -= limit
                        if (next.limit <= 0) iterator.remove()
                        modifiers.add(FieldCard)
                    }
                }
                return@execute done
            } else {
                while (iterator.hasNext()) {
                    next = iterator.next()
                    if (next.defaultCardType == defaultCardType) {
                        next.limit -= limit
                        if (next.limit <= 0) iterator.remove()
                        modifiers.add(FieldCard)
                        return@execute true
                    }
                }
            }
            return@execute false
        }

        fun isDone(majorType: MajorType) = execute {
            goal.getOrDefault(majorType, 0) <= project.getOrDefault(majorType, 0)
        }

        fun newIssue(majorType: MajorType, value: Int) = execute {
            issue.getOrPut(majorType) { LinkedList() }.add(value)
            modifiers.add(Project)
        }

        fun useCard(uuid: UUID) = execute {
            val index = heldCards.indexOfFirst { it.id == uuid }
            return@execute heldCards.removeAt(index)
        }

        fun useAbility(tierType: TierType): TierType? {
            if (playerData.tiers.contains(tierType) && useTier(tierType)) return tierType
            return null
        }

        fun setInt(key: DataKey, value: Int) = execute {
            dataIntMap[key] = value
            modifiers.add(DataInt)
        }

        fun setDouble(key: DataKey, value: Double) = execute {
            dataDoubleMap[key] = value
            modifiers.add(DataDouble)
        }

        fun addInt(key: DataKey, value: Int) {
            modifiers.add(DataInt)
            playerData.dataIntMap.merge(key, value, Integer::sum)
        }

        fun addDouble(key: DataKey, value: Double) {
            modifiers.add(DataDouble)
            playerData.dataDoubleMap.merge(key, value, java.lang.Double::sum)
        }

        fun getInt(key: DataKey): Int? = playerData.dataIntMap[key]

        fun getInt(key: DataKey, default: Int): Int = playerData.dataIntMap.getOrDefault(key, default)

        fun getDouble(key: DataKey): Double? = playerData.dataDoubleMap[key]

        fun getDouble(key: DataKey, default: Double): Double = playerData.dataDoubleMap.getOrDefault(key, default)

        fun removeInt(key: DataKey): Int? {
            modifiers.add(DataInt)
            return playerData.dataIntMap.remove(key)
        }

        fun removeDouble(key: DataKey): Double? {
            modifiers.add(DataDouble)
            return playerData.dataDoubleMap.remove(key)
        }

        fun build(): RawProtocol? {
            if (modifiers.isEmpty()) return null
            val array = execute {
                modifiers.mapNotNull {
                    when (it) {
                        Client -> client.toString()
                        Student -> objectMapper.writeValueAsString(GameStudentCards(students))
                        Deck -> deckList.size.toString()
                        HeldCard -> objectMapper.writeValueAsString(GameCards(heldCards))
                        Time -> time.toString()
                        FieldCard -> objectMapper.writeValueAsString(GameCards(fieldCards))

                        Project -> {
                            val json = JSONObject()
                            project.forEach { (key, value) -> json.put(key.toString(), value) }
                            json.toString()
                        }

                        Issue -> {
                            val json = JSONObject()
                            issue.forEach { (key, value) -> json.put(key.toString(), value) }
                            json.toString()
                        }

                        Sleep -> isSleep.toString()

                        DataInt -> {
                            val json = JSONObject()
                            dataIntMap.forEach { (key, value) -> json.put(key.toString(), value) }
                            json.toString()
                        }

                        DataDouble -> {
                            val json = JSONObject()
                            dataDoubleMap.forEach { (key, value) -> json.put(key.toString(), value) }
                            json.toString()
                        }

                        Students -> {
                            val list = modifiedStudent.mapNotNull { (_, modifier) -> modifier.build() }
                            if (list.isEmpty()) return@mapNotNull null
                            JSONObject().put("students", list).toString()
                        }

                        null -> null
                    }
                }.toTypedArray()
            }
            return RawProtocol(
                Protocol.Game.Server.DATA_UPDATE,
                modifiers.joinToString(separator = ",") { it.toString() },
                *array,
            )
        }
    }
}