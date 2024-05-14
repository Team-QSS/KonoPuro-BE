package kr.mooner510.konopuro.domain.socket.data.game

import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.dto.GameCard
import kr.mooner510.konopuro.domain.game.data.card.dto.GameStudentCard
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import kr.mooner510.konopuro.domain.socket.data.game.PlayerData.Modifier.*
import java.util.*
import kotlin.collections.ArrayList

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
    enum class Modifier {
        Client,
        Student,
        Deck,
        HeldCard,
        Time,
        FieldCard,
        Project,
        Issue,
        Sleep
    }

    class PlayerDataModifier(
        val todayLog: List<GameLog>,
        private val playerData: PlayerData
    ) {
        private val modifiers = EnumSet.noneOf(Modifier::class.java)
        lateinit var activeStudent: GameStudentCard

        fun <T> execute(run: PlayerData.() -> T): T {
            return run(playerData)
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
            project.merge(majorType, afterValue, Integer::sum)
            modifiers.add(Project)
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

        fun build(): RawProtocol {
            val array = execute {
                modifiers.mapNotNull {
                    when (it) {
                        Client -> client.toString()
                        Student -> ""
                        Deck -> ""
                        HeldCard -> ""
                        Time -> ""
                        FieldCard -> ""
                        Project -> ""
                        Issue -> ""
                        Sleep -> ""
                        null -> null
                    }
                }.toTypedArray()
            }
            return RawProtocol(
                Protocol.Game.Server.DATA_UPDATE,
                modifiers.joinToString(separator = ",") { it.toString() },
                *array
            )
        }
    }
}