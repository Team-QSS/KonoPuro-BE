package kr.mooner510.konopuro.domain.game.component

import kr.mooner510.konopuro.domain.game.data.card.entity.CardData
import kr.mooner510.konopuro.domain.game.data.card.entity.Passive
import kr.mooner510.konopuro.domain.game.data.card.entity.PlayerCard
import kr.mooner510.konopuro.domain.game.data.card.entity.Tier
import kr.mooner510.konopuro.domain.game.data.card.response.PassiveResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponse
import kr.mooner510.konopuro.domain.game.data.card.response.TierResponse
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.Gatcha
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaStack
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.game.exception.TierNotFoundException
import kr.mooner510.konopuro.domain.game.repository.CardDataRepository
import kr.mooner510.konopuro.domain.game.repository.PassiveRepository
import kr.mooner510.konopuro.domain.game.repository.TierMappingRepository
import kr.mooner510.konopuro.domain.game.repository.TierRepository
import kr.mooner510.konopuro.domain.socket.exception.CardDataNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class GatchaManager(
    private val tierRepository: TierRepository,
    private val cardDataRepository: CardDataRepository,
    private val passiveRepository: PassiveRepository,
    private val tierMappingRepository: TierMappingRepository
) {
    companion object {
        private var cardDataMap: HashMap<Long, CardData> = HashMap()
        private var cardMajorMap: EnumMap<MajorType, List<Long>> = EnumMap(MajorType::class.java)
        private var cardPassiveMapping: HashMap<Long, List<Passive>> = HashMap()
        private var cardTierMapping: HashMap<Long, List<List<Tier>>> = HashMap()

        fun Gatcha.gatchaOnce(stack: GatchaStack): PlayerCard {
            val id: Long
            val random = Math.random()
            val tier = when {
                random < .005 + stack.additionChance4() -> {
                    id = cardMajorMap[this.mainMajor]?.random() ?: cardDataMap.keys.random()
                    stack.stack4 = 0
                    4
                }

                random < .045 + stack.additionChance4() + stack.additionChance3() -> {
                    id = cardMajorMap[this.mainMajor]?.random() ?: cardDataMap.keys.random()
                    stack.stack3 = 0
                    3
                }

                else -> {
                    id = cardDataMap.keys.random()
                    2
                }
            }
            return PlayerCard(
                id,
                cardTierMapping[id]?.get(1)?.random()?.id,
                if (tier >= 3) cardTierMapping[id]?.get(2)?.random()?.id else null,
                if (tier >= 4) cardTierMapping[id]?.get(3)?.random()?.id else null
            )
        }
    }

    init {
        update()
    }

    final fun update() {
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
            cardData.groupSet().forEach {
                majorMap[it]?.add(cardData.id)
            }
            cardDataMap[cardData.id] = cardData
            val tierTopList = mutableListOf<List<Tier>>()
            for (tier in 1..4) {
                tierTopList.add(tierRepository.findAllById(tierMappingRepository.findByCardDataId(cardData.id).map { it.tierId }).toList())
            }
            cardTierMapping[cardData.id] = tierTopList
            cardPassiveMapping[cardData.id] =
                passiveRepository.findAllById(listOfNotNull(cardData.passiveFirst, cardData.passiveSecond, cardData.passiveThird)).toList()
        }
        majorMap.forEach { (key, value) ->
            cardMajorMap[key] = value
        }

        Runtime.getRuntime().gc()
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
        stack.stack4++
        stack.stack3++

        val playerCard = gatcha.gatchaOnce(stack)

        val cardData = cardDataRepository.findByIdOrNull(playerCard.cardDataId) ?: throw CardDataNotFoundException()

        val defaultPassives = passiveRepository.findAllById(
            listOfNotNull(cardData.passiveFirst, cardData.passiveSecond, cardData.passiveThird)
        )
        val (passives, tiers) = playerCard.split(cardData, passiveRepository, tierRepository)

        return PlayerCardResponse(
            cardData.title,
            cardData.description,
            cardData.groupSet().toList(),
            playerCard.getTier(),
            cardData.type,
            defaultPassives.map { PassiveResponse(it.id, it.title, it.description) },
            passives.map { PassiveResponse(it.id, it.title, it.description) },
            tiers.map { TierResponse(it.id, it.title, it.description, it.time) }
        )
    }
}