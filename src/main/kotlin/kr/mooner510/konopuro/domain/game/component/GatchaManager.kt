package kr.mooner510.konopuro.domain.game.component

import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.StudentCardType
import kr.mooner510.konopuro.domain.game.data.card.entity.StudentCardData
import kr.mooner510.konopuro.domain.game.data.card.entity.Passive
import kr.mooner510.konopuro.domain.game.data.card.entity.PlayerStudentCard
import kr.mooner510.konopuro.domain.game.data.card.entity.Tier
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponse
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.Gatcha
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaLog
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaStack
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.game.exception.GatchaExpiredException
import kr.mooner510.konopuro.domain.game.exception.TierNotFoundException
import kr.mooner510.konopuro.domain.game.repository.*
import kr.mooner510.konopuro.domain.game.utils.PassiveTierUtils.toResponse
import kr.mooner510.konopuro.domain.socket.exception.CardDataNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

@Component
class GatchaManager(
    private val tierRepository: TierRepository,
    private val cardDataRepository: CardDataRepository,
    private val passiveRepository: PassiveRepository,
    private val tierMappingRepository: TierMappingRepository,
    private val playerCardRepository: PlayerCardRepository,
    private val gatchaLogRepository: GatchaLogRepository,
    private val passiveMappingRepository: PassiveMappingRepository
) {
    companion object {
        private val cardMajorMap: EnumMap<MajorType, MutableList<StudentCardType>> = EnumMap(MajorType::class.java)
//        private var cardPassiveMapping: HashMap<Long, List<Passive>> = HashMap()
//        private var cardTierMapping: HashMap<Long, List<List<Tier>>> = HashMap()

        fun Gatcha.gatchaOnce(stack: GatchaStack): PlayerStudentCard? {
            val random = Random.nextDouble()
            val majorTypes = cardMajorMap.keys.filter { it != this.mainMajor }
            when {
                random < stack.chance4() -> {
                    val major = if (stack.full4 || Random.nextDouble() < 0.5) this.mainMajor else majorTypes.random()
                    stack.full4 = major != this.mainMajor
                    val id = cardMajorMap[major]?.random() ?: StudentCardType.entries.random()
                    println("4 Tier: ${stack.chance4()}, 3 Tier: ${stack.chance3()} :: 4 Tier! $random")
                    stack.stack4 = 0
                    return PlayerStudentCard(
                        stack.userId,
                        id,
                        id.secondTier.random(),
                        id.thirdPassive.random(),
                        id.forthTier.random()
                    )
                }

                random < stack.chance4() + stack.chance3() -> {
                    val major =
                        if (stack.full3 || Random.nextDouble() < 0.5) this.mainMajor else if (Math.random() < 0.5) majorTypes.random() else null
                    stack.full3 = major != this.mainMajor
                    val id = cardMajorMap[major]?.random() ?: StudentCardType.entries.random()
                    stack.stack3 = 0
                    return PlayerStudentCard(
                        stack.userId,
                        id,
                        id.secondTier.random(),
                        id.thirdPassive.random(),
                        null
                    )
                }
            }
            if (Random.nextDouble() < .25) {
                val major = MajorType.entries.random()
                val id = cardMajorMap[major]?.random() ?: StudentCardType.entries.random()
                return PlayerStudentCard(
                    stack.userId,
                    id,
                    id.secondTier.random(),
                    null,
                    null
                )
            }
            return null
        }
    }

    init {
        println("=========")
        MajorType.entries.forEach {
            cardMajorMap[it] = mutableListOf()
        }
        StudentCardType.entries.forEach { studentCardType ->
            studentCardType.major.forEach {
                cardMajorMap[it]!!.add(studentCardType)
            }
        }
        println("=========")
    }

    fun HashMap<Long, Tier>.append(key: Long): Tier {
        var data = this[key]
        if (data == null) {
            data = tierRepository.findByIdOrNull(key) ?: throw TierNotFoundException()
            this[key] = data
        }
        return data
    }

    fun gatcha(gatcha: Gatcha, stack: GatchaStack): PlayerCardResponse {
        val now = LocalDateTime.now()
        if (now !in gatcha.startAt..gatcha.endAt) throw GatchaExpiredException()

        stack.stack4++
        stack.stack3++

        val stack4 = stack.stack4
        val stack3 = stack.stack3
        val playerCard = playerCardRepository.save(gatcha.gatchaOnce(stack))

        val cardData = cardDataRepository.findByIdOrNull(playerCard.cardDataId) ?: throw CardDataNotFoundException()

        val (passives, tiers) = playerCard.split(cardData, passiveRepository, tierRepository)

        gatchaLogRepository.save(
            GatchaLog(
                stack.userId,
                cardData.id,
                playerCard.getTier(),
                if (playerCard.getTier() == 3) stack3 else if (playerCard.getTier() == 4) stack4 else null,
            )
        )

        return PlayerCardResponse(
            playerCard.id,
            cardData.title,
            cardData.description,
            cardData.groupSet().toList(),
            playerCard.getTier(),
            cardData.type,
            passives.toResponse(),
            tiers.toResponse()
        )
    }
}