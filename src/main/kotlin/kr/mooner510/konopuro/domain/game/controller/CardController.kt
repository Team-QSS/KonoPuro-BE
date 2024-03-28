package kr.mooner510.konopuro.domain.game.controller

import kr.mooner510.konopuro.domain.game.data.card.entity.CardData
import kr.mooner510.konopuro.domain.game.data.card.entity.Passive
import kr.mooner510.konopuro.domain.game.data.card.entity.Tier
import kr.mooner510.konopuro.domain.game.data.card.request.CreateCardRequest
import kr.mooner510.konopuro.domain.game.data.card.response.CardDataResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PassiveResponse
import kr.mooner510.konopuro.domain.game.data.card.response.TierResponse
import kr.mooner510.konopuro.domain.game.exception.CardNotFoundException
import kr.mooner510.konopuro.domain.game.repository.CardDataRepository
import kr.mooner510.konopuro.domain.game.repository.PassiveRepository
import kr.mooner510.konopuro.domain.game.repository.TierRepository
import kr.mooner510.konopuro.global.security.exception.InvalidParameterException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.pow

@RestController
@RequestMapping("/api/card")
class CardController(
    private val cardDataRepository: CardDataRepository,
    private val tierRepository: TierRepository,
    private val passiveRepository: PassiveRepository
) {
    @GetMapping
    fun getCardData(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) id: Long?
    ): CardDataResponse {
        val cardData: CardData = (id?.let { cardDataRepository.findById(id) }
            ?: name?.let { cardDataRepository.findByTitleStartsWith(it) }
            ?: throw InvalidParameterException()).getOrNull()
            ?: throw CardNotFoundException()

        val tiers = tierRepository.findByIdOrNull(cardData.startTier)

        val passives = passiveRepository.findAllById(
            listOfNotNull(cardData.passiveFirst, cardData.passiveSecond, cardData.passiveThird)
        )

        return CardDataResponse(
            cardData.title,
            cardData.description,
            cardData.groupSet().toList(),
            cardData.type,
            passives.map { PassiveResponse(it.id, it.title, it.description) },
            listOfNotNull(tiers?.let { TierResponse(it.id, it.title, it.description, it.time) })
        )
    }

    @PostMapping
    fun createCardData(
        @RequestBody req: CreateCardRequest
    ): CardDataResponse {
        val tiers = req.tiers.map {
            tierRepository.save(
                Tier(
                    it.title,
                    it.description,
                    it.time
                )
            )
        }

        val passives = req.passives.map {
            passiveRepository.save(
                Passive(
                    it.title,
                    it.description
                )
            )
        }

        val cardData = cardDataRepository.save(
            CardData(
                req.title,
                req.description,
                req.cardGroups.sumOf { 2.0.pow(it.ordinal) }.toLong(),
                req.type,
                tiers[0].id,
                passives[0].id,
                if (passives.size > 1) passives[1].id else null,
                if (passives.size > 2) passives[2].id else null
            )
        )

        return CardDataResponse(
            cardData.title,
            cardData.description,
            cardData.groupSet().toList(),
            cardData.type,
            passives.map { PassiveResponse(it.id, it.title, it.description) },
            tiers.map { TierResponse(it.id, it.title, it.description, it.time) }
        )
    }
}