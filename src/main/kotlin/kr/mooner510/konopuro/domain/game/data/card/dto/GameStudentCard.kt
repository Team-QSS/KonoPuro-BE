package kr.mooner510.konopuro.domain.game.data.card.dto

import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.StudentCardType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.manager.CardManager.decreaseFatigue
import kr.mooner510.konopuro.domain.game.data.card.manager.CardManager.increaseFatigue
import kr.mooner510.konopuro.domain.game.data.card.types.StudentState
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.socket.data.game.PlayerData
import org.json.JSONObject
import java.util.*
import kotlin.math.max
import kotlin.math.min

data class GameStudentCard(
    val id: UUID,
    val cardType: StudentCardType,
    val groups: Set<MajorType>,
    val tiers: EnumSet<TierType>,
    val passives: EnumSet<PassiveType>
) {
    private var fatigue: Double = 0.0

    private val dataInt = EnumMap<StudentState, Int>(StudentState::class.java)

    enum class Modifier {
        Fatigue,
        Int
    }

    fun getFatigue() = fatigue

    fun getEndDate(key: StudentState): Int? {
        return dataInt[key]
    }

    fun getEndDate(key: StudentState, default: Int): Int {
        return dataInt.getOrDefault(key, default)
    }

    fun checkEndDate(key: StudentState, now: Int): Boolean {
        return now <= getEndDate(key, 0)
    }

    fun hasEndDate(key: StudentState): Boolean {
        return dataInt.containsKey(key)
    }

    class GameStudentCardModifier(
        private val date: Int,
        private val gameStudentCard: GameStudentCard,
        private val ownerData: PlayerData
    ) {
        private val modifier = EnumSet.noneOf(Modifier::class.java)

        fun <T> execute(run: PlayerData.() -> T): T {
            return run(ownerData)
        }

        fun setFatigue(value: Double) {
            gameStudentCard.fatigue = value
            modifier.add(Modifier.Fatigue)
        }

        fun addFatigue(value: Double) {
            gameStudentCard.fatigue = min(6.0, gameStudentCard.fatigue + max(0.0, increaseFatigue(value, gameStudentCard.fatigue)))
            modifier.add(Modifier.Fatigue)
        }

        fun removeFatigue(value: Double) {
            if (gameStudentCard.fatigue <= 0) return
            gameStudentCard.fatigue = max(0.0, gameStudentCard.fatigue - max(0.0,  decreaseFatigue(value, gameStudentCard.fatigue)))
            modifier.add(Modifier.Fatigue)
        }

        fun removeIfEndDate(now: Int) {
            val iterator = gameStudentCard.dataInt.iterator()
            iterator.forEachRemaining {
                if (it.value < now) {
                    iterator.remove()
                    modifier.add(Modifier.Int)
                }
            }
        }

        fun setEndDate(key: StudentState, value: Int) {
            gameStudentCard.dataInt[key] = date + value
            modifier.add(Modifier.Int)
        }

        fun addEndDate(key: StudentState, value: Int) {
            gameStudentCard.dataInt.merge(key, value, Integer::sum)
            modifier.add(Modifier.Int)
        }

        fun build(): JSONObject? {
            if (modifier.isEmpty()) return null
            val json = JSONObject().put("id", gameStudentCard.id.toString())
            if (modifier.contains(Modifier.Fatigue)) json.put("fatigue", gameStudentCard.getFatigue())
            if (modifier.contains(Modifier.Int)) {
                val data = JSONObject()
                gameStudentCard.dataInt.forEach { (key, value) -> data.put(key.toString(), value) }
                json.put("data", data)
            }
            return json
        }
    }
}
