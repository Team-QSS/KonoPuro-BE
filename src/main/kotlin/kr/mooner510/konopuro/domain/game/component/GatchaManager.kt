package kr.mooner510.konopuro.domain.game.component

import kr.mooner510.konopuro.domain.game.data.card.entity.CardData
import kr.mooner510.konopuro.domain.game.data.card.entity.Passive
import kr.mooner510.konopuro.domain.game.data.card.entity.PlayerCard
import kr.mooner510.konopuro.domain.game.data.card.entity.Tier
import kr.mooner510.konopuro.domain.game.data.card.response.PassiveResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponse
import kr.mooner510.konopuro.domain.game.data.card.response.TierResponse
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.Gatcha
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaLog
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaStack
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.game.exception.GatchaExpiredException
import kr.mooner510.konopuro.domain.game.exception.TierNotFoundException
import kr.mooner510.konopuro.domain.game.repository.*
import kr.mooner510.konopuro.domain.game.utils.PassiveTierUtils.toResponse
import kr.mooner510.konopuro.domain.socket.exception.CardDataNotFoundException
import org.springframework.cglib.core.Local
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

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
        private var cardDataMap: HashMap<Long, CardData> = HashMap()
        private var cardMajorMap: EnumMap<MajorType, List<Long>> = EnumMap(MajorType::class.java)
        private var cardPassiveMapping: HashMap<Long, List<Passive>> = HashMap()
        private var cardTierMapping: HashMap<Long, List<List<Tier>>> = HashMap()

        fun Gatcha.gatchaOnce(stack: GatchaStack): PlayerCard {
            val id: Long
            val random = Math.random()
            val majorTypes = cardMajorMap.keys.filter { it != this.mainMajor }
            val tier = when {
                random < stack.chance4() -> {
                    val major = if (stack.full4 || Math.random() < 0.5) this.mainMajor else majorTypes.random()
                    stack.full4 = major != this.mainMajor
                    id = cardMajorMap[major]?.random()
                        ?: cardDataMap.keys.random()
                    println("4 Tier: ${stack.chance4()}, 3 Tier: ${stack.chance3()} :: 4 Tier! $random")
                    stack.stack4 = 0
                    4
                }

                random < stack.chance4() + stack.chance3() -> {
                    val major = if (stack.full3 || Math.random() < 0.5) this.mainMajor else majorTypes.random()
                    stack.full3 = major != this.mainMajor
                    id = cardMajorMap[major]?.random()
                        ?: cardDataMap.keys.random()
                    println("4 Tier: ${stack.chance4()}, 3 Tier: ${stack.chance3()} :: 3 Tier! $random")
                    stack.stack3 = 0
                    3
                }

                else -> {
                    id = cardDataMap.keys.random()
                    println("4 Tier: ${stack.chance4()}, 3 Tier: ${stack.chance3()}")
                    2
                }
            }
            return PlayerCard(
                stack.userId,
                id,
                cardTierMapping[id]?.get(0)?.random()?.id,
                if (tier >= 3) cardPassiveMapping[id]?.random()?.id else null,
                if (tier >= 4) cardTierMapping[id]?.get(1)?.random()?.id else null
            )
        }
    }

    init {
        update()
        println("=========")
        cardMajorMap.forEach { (key, value) ->
            println("$key: $value")
        }
        println("=========")
        cardDataMap.forEach { (key, value) ->
            println("$key: ${value.title}")
        }
        println("=========")
    }

    final fun update() {
        Runtime.getRuntime().gc()
        cardMajorMap = EnumMap(MajorType::class.java)
        val majorMap = EnumMap<MajorType, MutableList<Long>>(MajorType::class.java)
        MajorType.entries.forEach {
            majorMap[it] = mutableListOf()
        }
        cardPassiveMapping = HashMap()
        cardDataMap = HashMap()
        cardTierMapping = HashMap()

        val cardDataList = cardDataRepository.findAll()
        cardDataList.forEach { cardData ->
            cardData.groupSet().forEach { major ->
                majorMap[major]?.add(cardData.id)
            }
            cardDataMap[cardData.id] = cardData
            cardTierMapping[cardData.id] = mutableListOf(
                tierRepository.findAllById(
                    tierMappingRepository.findByCardDataIdAndTier(cardData.id, 2).map { it.tierId }
                ).toList(),
                tierRepository.findAllById(
                    tierMappingRepository.findByCardDataIdAndTier(cardData.id, 4).map { it.tierId }
                ).toList()
            )
            cardPassiveMapping[cardData.id] =
                passiveRepository.findAllById(passiveMappingRepository.findByCardDataId(cardData.id).map { it.passiveId }).toList()
        }
        majorMap.forEach { (key, value) ->
            if (value.isNotEmpty()) cardMajorMap[key] = value
        }
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