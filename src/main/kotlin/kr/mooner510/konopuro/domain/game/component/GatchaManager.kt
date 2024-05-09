package kr.mooner510.konopuro.domain.game.component

import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.StudentCardType
import kr.mooner510.konopuro.domain.game.data.card.entity.PlayerCard
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponse
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.Gatcha
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaLog
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaStack
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.game.exception.GatchaExpiredException
import kr.mooner510.konopuro.domain.game.repository.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

@Component
class GatchaManager(
    private val playerCardRepository: PlayerCardRepository,
    private val gatchaLogRepository: GatchaLogRepository,
) {
    companion object {
        private val cardMajorMap: EnumMap<MajorType, MutableList<StudentCardType>> = EnumMap(MajorType::class.java)

        fun Gatcha.gatchaOnce(stack: GatchaStack): PlayerCard {
            val random = Random.nextDouble()
            val majorTypes = cardMajorMap.keys.filter { it != this.mainMajor }
            when {
                random < stack.chance4() -> {
                    val major = if (stack.full4 || Random.nextDouble() < 0.5) this.mainMajor else majorTypes.random()
                    stack.full4 = major != this.mainMajor
                    val id = cardMajorMap[major]?.random() ?: StudentCardType.entries.random()
                    stack.stack4 = 0
                    return PlayerCard(
                        stack.userId,
                        id.toString(),
                        true,
                        id.secondTier.random(),
                        id.thirdPassive.random(),
                        id.forthTier.random()
                    )
                }

                random < stack.chance4() + stack.chance3() -> {
                    val major =
                        if (stack.full3 || Random.nextDouble() < 0.5) this.mainMajor
                        else if (Random.nextDouble() < 0.5) majorTypes.random()
                        else null
                    if (major == null) {
                        return PlayerCard(
                            stack.userId,
                            DefaultCardType.tier3List.random().toString()
                        )
                    }
                    stack.full3 = major != this.mainMajor
                    val id = cardMajorMap[major]?.random() ?: StudentCardType.entries.random()
                    stack.stack3 = 0
                    return PlayerCard(
                        stack.userId,
                        id.toString(),
                        true,
                        id.secondTier.random(),
                        id.thirdPassive.random()
                    )
                }
            }
            if (Random.nextDouble() < .25) {
                val major = MajorType.entries.random()
                val id = cardMajorMap[major]?.random() ?: StudentCardType.entries.random()
                return PlayerCard(
                    stack.userId,
                    id.toString(),
                    true,
                    id.secondTier.random()
                )
            }
            return PlayerCard(
                stack.userId,
                DefaultCardType.tierOtherList.random().toString()
            )
        }
    }

    init {
        MajorType.entries.forEach {
            cardMajorMap[it] = mutableListOf()
        }
        StudentCardType.entries.forEach { studentCardType ->
            studentCardType.major.forEach {
                cardMajorMap[it]!!.add(studentCardType)
            }
        }
    }

    fun gatcha(gatcha: Gatcha, stack: GatchaStack): PlayerCardResponse {
        val now = LocalDateTime.now()
        if (now !in gatcha.startAt..gatcha.endAt) throw GatchaExpiredException()

        stack.stack4++
        stack.stack3++

        val stack4 = stack.stack4
        val stack3 = stack.stack3
        val playerCard = playerCardRepository.save(gatcha.gatchaOnce(stack))

        gatchaLogRepository.save(
            GatchaLog(
                stack.userId,
                playerCard.cardType,
                playerCard.isStudent,
                playerCard.getTier(),
                if (playerCard.getTier() == 3) stack3 else if (playerCard.getTier() == 4) stack4 else null,
            )
        )

        return playerCard.toResponse()
    }
}