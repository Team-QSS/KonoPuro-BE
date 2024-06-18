package kr.mooner510.konopuro.domain.socket.data.game

import com.fasterxml.jackson.databind.ObjectMapper
import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.dto.GameCard
import kr.mooner510.konopuro.domain.game.data.card.dto.GameStudentCard
import kr.mooner510.konopuro.domain.game.data.card.manager.CardManager.calculateProject
import kr.mooner510.konopuro.domain.game.data.card.manager.CardManager.onNewDay
import kr.mooner510.konopuro.domain.game.data.card.manager.CardManager.onNewDayAfter
import kr.mooner510.konopuro.domain.game.data.card.manager.CardManager.useDefaultCard
import kr.mooner510.konopuro.domain.game.data.card.manager.CardManager.useTier
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import kr.mooner510.konopuro.domain.socket.data.game.PlayerData.Modifier.*
import kr.mooner510.konopuro.domain.socket.data.obj.GameCards
import kr.mooner510.konopuro.domain.socket.data.obj.GameStudentCards
import kr.mooner510.konopuro.domain.socket.data.type.DataKey
import kr.mooner510.konopuro.global.utils.UUIDParser
import org.json.JSONObject
import java.util.*

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
    private val dateMap = EnumMap<DataKey, Int>(DataKey::class.java)
    private val dataMap = EnumMap<DataKey, Int>(DataKey::class.java)
    private val dataProjectAdditionMap = HashMap<Pair<MajorType, String>, Pair<Int, Int>>()

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
        DateData,
        ProjectAdditionData,
        Students
    }

    override fun equals(other: Any?): Boolean {
        if (other is PlayerData) return other.id == this.id
        return super.equals(other)
    }

    class PlayerDataModifierGroup(
        gameRoom: GameRoom,
        selfData: PlayerData,
        otherData: PlayerData,
    ) {
        private val selfModifier = PlayerDataModifier(gameRoom, selfData)
        private val otherModifier = PlayerDataModifier(gameRoom, otherData)

        init {
            selfModifier.registerOther(otherModifier)
            otherModifier.registerOther(selfModifier)
        }

        fun self(func: (PlayerDataModifier) -> Unit) {
            func(selfModifier)
        }

        fun other(func: (PlayerDataModifier) -> Unit) {
            func(otherModifier)
        }

        fun all(func: (PlayerDataModifier) -> Unit) {
            func(selfModifier)
            func(otherModifier)
        }

        fun build(): Pair<List<String>?, List<String>?> {
            return selfModifier.build() to otherModifier.build()
        }
    }

    class PlayerDataModifier(
        private val gameRoom: GameRoom,
        private val playerData: PlayerData
    ) {
        companion object {
            private val objectMapper = ObjectMapper()
        }

        fun registerOther(otherModifier: PlayerDataModifier) {
            this.otherModifier = otherModifier
        }

        lateinit var otherModifier: PlayerDataModifier

        private val modifiers = EnumSet.noneOf(Modifier::class.java)
        lateinit var activeStudent: GameStudentCard
        private var modifiedStudent: HashMap<UUID, GameStudentCard.GameStudentCardModifier> = hashMapOf()

        fun <T> execute(run: PlayerData.() -> T): T {
            return run(playerData)
        }

        fun applyStudentData() {
            modifiers.add(Student)
        }

        fun modifyStudents(run: (GameStudentCard.GameStudentCardModifier) -> Unit) {
            modifiers.add(Students)
            playerData.students.forEach {
                val modifier = GameStudentCard.GameStudentCardModifier(gameRoom.date, it, playerData)
                run(modifier)
                modifiedStudent[it.id] = modifier
            }
        }

        fun modifyStudent(id: UUID, run: (GameStudentCard.GameStudentCardModifier) -> Unit) {
            modifiers.add(Students)
            run(modifiedStudent.getOrPut(id) {
                GameStudentCard.GameStudentCardModifier(
                    gameRoom.date,
                    playerData.students.find { it.id == id }!!,
                    playerData
                )
            })
        }

        fun modifyStudent(studentCard: GameStudentCard, run: (GameStudentCard.GameStudentCardModifier) -> Unit) {
            modifiers.add(Students)
            run(modifiedStudent.getOrPut(studentCard.id) {
                GameStudentCard.GameStudentCardModifier(
                    gameRoom.date,
                    studentCard,
                    playerData
                )
            })
        }

        fun newDay(date: Int) = execute {
            repeat(1) { pickupDeck() }
            DataKey.removals.forEach(::remove)
            onNewDay()
            fieldCards.mapNotNull { if (it.dayDuration) it.defaultCardType else null }.toSet().forEach {
                removeFieldCardLimit(it)
            }
            onNewDayAfter()
            modifyStudents {
                it.removeIfEndDate(date)
                it.removeFatigue(time * 0.2)
            }
            time = 0
            addTime(24)
            isSleep = false
        }

        fun setClient(uuid: UUID) = execute {
            client = uuid
            modifiers.add(Client)
        }

        fun pickupDeck(): GameCard? = execute {
            if (deckList.isEmpty()) return@execute null
            val card = deckList.removeFirst()
            heldCards.add(card)
            modifiers.add(Deck)
            modifiers.add(HeldCard)
            return@execute card
        }

        fun addTime(value: Int) = execute {
            if (value <= 0) return@execute
            this.time += value
            modifiers.add(Time)
        }

        fun removeTime(time: Int) = execute {
            if (this.time < time) return@execute false
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
            var afterValue = value + calculateProject(majorType)
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
            add(majorType.dataTotalKey, value)
            add(majorType.dataKey, value)
            project.merge(majorType, afterValue, Integer::sum)
            modifiers.add(Project)
        }

        fun addFieldCard(
            defaultCardType: DefaultCardType,
            duration: Int,
            dupe: Boolean = false,
            isDayDuration: Boolean = false
        ) = execute {
            if (dupe) fieldCards.add(GameCard(UUIDParser.nilUUID, defaultCardType, duration, isDayDuration))
            else {
                fieldCards.find { it.defaultCardType == defaultCardType }?.let {
                    it.duration = duration
                    return@execute
                }
                fieldCards.add(GameCard(UUIDParser.nilUUID, defaultCardType, duration, isDayDuration))
            }
        }

        fun removeFieldCard(defaultCardType: DefaultCardType) = execute {
            fieldCards.removeIf { it.defaultCardType == defaultCardType }
            modifiers.add(FieldCard)
        }

        fun removeFieldCardLimit(defaultCardType: DefaultCardType, limit: Int = 1, multi: Boolean = true): Boolean =
            execute {
                val iterator = fieldCards.listIterator()
                var next: GameCard
                if (multi) {
                    var done = false
                    while (iterator.hasNext()) {
                        next = iterator.next()
                        if (next.defaultCardType == defaultCardType) {
                            done = true
                            next.duration -= limit
                            if (next.duration <= 0) iterator.remove()
                            modifiers.add(FieldCard)
                        }
                    }
                    return@execute done
                } else {
                    while (iterator.hasNext()) {
                        next = iterator.next()
                        if (next.defaultCardType == defaultCardType) {
                            next.duration -= limit
                            if (next.duration <= 0) iterator.remove()
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
            val card = heldCards[index]
            if (!removeTime(card.defaultCardType.time)) return@execute null
            useDefaultCard(card.defaultCardType)
            return@execute heldCards.removeAt(index)
        }

        fun useAbility(tierType: TierType): TierType? {
            if (playerData.tiers.contains(tierType) && useTier(tierType)) return tierType
            return null
        }

        fun set(key: DataKey, value: Int) {
            playerData.dataMap[key] = value
            modifiers.add(DateData)
        }

        fun add(key: DataKey, value: Int) {
            playerData.dataMap.merge(key, value, Integer::sum)
            modifiers.add(DateData)
        }

        fun get(key: DataKey, default: Int): Int {
            return playerData.dataMap.getOrElse(key) { default }
        }

        fun get(key: DataKey): Int? {
            return playerData.dataMap[key]
        }

        fun remove(key: DataKey) {
            if (playerData.dataMap.remove(key) != null) {
                modifiers.add(DateData)
            }
        }

        fun setDate(key: DataKey, duration: Int) {
            playerData.dateMap[key] = gameRoom.date + duration
            modifiers.add(DateData)
        }

        fun addDate(key: DataKey, duration: Int) {
            playerData.dateMap.merge(key, duration, Integer::sum)
            modifiers.add(DateData)
        }

        fun removeDate(key: DataKey) {
            if (playerData.dateMap.remove(key) != null) {
                modifiers.add(DateData)
            }
        }

        fun removeDate(key: DataKey, duration: Int) {
            playerData.dateMap[key]?.let {
                if (it - duration <= 0) {
                    if (playerData.dateMap.remove(key) != null) {
                        modifiers.add(DateData)
                    }
                    return
                }
            }
            playerData.dateMap.merge(key, -duration, Integer::sum)
            modifiers.add(DateData)
        }

        fun setMajor(major: MajorType, str: String, duration: Int, addition: Int) {
            playerData.dataProjectAdditionMap[major to str] = gameRoom.date + duration to addition
            modifiers.add(ProjectAdditionData)
        }

        fun addMajor(major: MajorType, str: String, duration: Int, addition: Int) {
            playerData.dataProjectAdditionMap.merge(
                major to str,
                gameRoom.date + duration to addition
            ) { (a, b), (c, d) ->
                a + c - gameRoom.date to b + d
            }
            modifiers.add(ProjectAdditionData)
        }

        fun addMajorDuration(major: MajorType, str: String, duration: Int) {
            addMajor(major, str, duration, 0)
        }

        fun addMajorAddition(major: MajorType, str: String, addition: Int) {
            addMajor(major, str, 0, addition)
        }

        fun removeMajor(major: MajorType, str: String) {
            playerData.dataProjectAdditionMap.remove(major to str)
            modifiers.add(ProjectAdditionData)
        }

        fun setMajor(major: MajorType, passive: PassiveType, duration: Int, addition: Int) {
            setMajor(major, passive.toString(), duration, addition)
        }

        fun addMajor(major: MajorType, passive: PassiveType, duration: Int, addition: Int) {
            addMajor(major, passive.toString(), duration, addition)
        }

        fun addMajorDuration(major: MajorType, passive: PassiveType, duration: Int) {
            addMajor(major, passive, duration, 0)
        }

        fun addMajorAddition(major: MajorType, passive: PassiveType, addition: Int) {
            addMajor(major, passive, 0, addition)
        }

        fun removeMajor(major: MajorType, passive: PassiveType) {
            removeMajor(major, passive.toString())
        }

        fun setMajor(major: MajorType, tier: TierType, duration: Int, addition: Int) {
            setMajor(major, tier.toString(), duration, addition)
        }

        fun addMajor(major: MajorType, tier: TierType, duration: Int, addition: Int) {
            addMajor(major, tier.toString(), duration, addition)
        }

        fun addMajorDuration(major: MajorType, tier: TierType, duration: Int) {
            addMajor(major, tier, duration, 0)
        }

        fun addMajorAddition(major: MajorType, tier: TierType, addition: Int) {
            addMajor(major, tier, 0, addition)
        }

        fun removeMajor(major: MajorType, tier: TierType) {
            removeMajor(major, tier.toString())
        }


        fun build(): List<String>? {
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

                        DateData -> {
                            val json = JSONObject()
                            dateMap.forEach { (key, value) -> json.put(key.toString(), value) }
                            json.toString()
                        }

                        ProjectAdditionData -> {
                            val json = JSONObject()
                            val map = EnumMap<MajorType, HashMap<String, Pair<Int, Int>>>(MajorType::class.java)
                            dataProjectAdditionMap.forEach { (key, value) ->
                                map.getOrPut(key.first) { hashMapOf() }[key.second] = value
                            }
                            map.forEach { (key, value) ->
                                val inner = JSONObject()
                                value.forEach { (key2, value2) ->
                                    inner.put(key2, value2.toList())
                                }
                                json.put(key.toString(), inner)
                            }
                            json.toString()
                        }

                        Students -> {
                            val list = modifiedStudent.mapNotNull { (_, modifier) -> modifier.build() }
                            if (list.isEmpty()) {
                                modifiers.remove(Students)
                                return@mapNotNull null
                            }
                            JSONObject().put("students", list).toString()
                        }

                        null -> null
                    }
                }.toTypedArray()
            }
            return listOf(
                modifiers.joinToString(separator = ",") { it.toString() },
                *array,
            )
        }
    }
}
