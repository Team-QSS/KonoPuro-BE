package kr.mooner510.konopuro.domain.game.controller

import jakarta.transaction.Transactional
import kr.mooner510.konopuro.domain.game.data.card.entity.*
import kr.mooner510.konopuro.domain.game.data.card.request.CreateCardRequest
import kr.mooner510.konopuro.domain.game.data.card.response.CardDataResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PassiveResponse
import kr.mooner510.konopuro.domain.game.data.card.response.TierResponse
import kr.mooner510.konopuro.domain.game.exception.CardNotFoundException
import kr.mooner510.konopuro.domain.game.exception.PassiveNotFoundException
import kr.mooner510.konopuro.domain.game.repository.*
import kr.mooner510.konopuro.global.security.exception.InvalidParameterException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/card")
class CardController(
    private val cardDataRepository: CardDataRepository,
    private val tierRepository: TierRepository,
    private val passiveRepository: PassiveRepository,
    private val tierMappingRepository: TierMappingRepository,
    private val passiveMappingRepository: PassiveMappingRepository
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

        val tiers = tierMappingRepository.findByCardDataId(cardData.id).groupBy({ it.tier }, { tierRepository.findByIdOrNull(it.tierId) })
        val passives = passiveRepository.findAllById(listOfNotNull(cardData.passiveFirst, cardData.passiveSecond, cardData.passiveThird))
        val passive = passiveMappingRepository.findByCardDataId(cardData.id).mapNotNull { passiveRepository.findByIdOrNull(it.passiveId) }

        return CardDataResponse(
            cardData.title,
            cardData.description,
            cardData.groupSet().toList(),
            cardData.type,
            passives.map { PassiveResponse(it.id, it.title, it.description) },
            tiers[2]?.mapNotNull { tier -> tier?.let { TierResponse(it.id, it.title, it.description, it.time) } } ?: emptyList(),
            passive.map { PassiveResponse(it.id, it.title, it.description) },
            tiers[4]?.mapNotNull { tier -> tier?.let { TierResponse(it.id, it.title, it.description, it.time) } } ?: emptyList(),
        )
    }

    @PostMapping
    @Transactional
    fun createCardData(
        @RequestBody req: CreateCardRequest
    ): CardDataResponse {
        val tier2 = req.tier2.map {
            tierRepository.save(
                Tier(
                    it.title,
                    it.description,
                    it.time
                )
            )
        }

        val tier4 = req.tier2.map {
            tierRepository.save(
                Tier(
                    it.title,
                    it.description,
                    it.time
                )
            )
        }

        val defaultPassives = req.defaultPassives.map {
            passiveRepository.save(
                Passive(
                    it.title,
                    it.description
                )
            )
        }

        val additionPassives = req.additionPassive.map {
            passiveRepository.save(Passive(it.title, it.description))
        }

        val cardData = cardDataRepository.save(
            CardData(
                req.title,
                req.description,
                req.cardGroups.sumOf { 1L shl it.ordinal },
                req.type,
                req.startTierId,
                defaultPassives[0].id,
                if (defaultPassives.size > 1) defaultPassives[1].id else null,
                if (defaultPassives.size > 2) defaultPassives[2].id else null
            )
        )

        tier2.map { tierMappingRepository.save(TierMapping(it.id, cardData.id, 2)) }
        tier4.map { tierMappingRepository.save(TierMapping(it.id, cardData.id, 4)) }
        additionPassives.map { passiveMappingRepository.save(PassiveMapping(it.id, cardData.id)) }

        return CardDataResponse(
            cardData.title,
            cardData.description,
            cardData.groupSet().toList(),
            cardData.type,
            defaultPassives.map { PassiveResponse(it.id, it.title, it.description) },
            tier2.map { TierResponse(it.id, it.title, it.description, it.time) },
            additionPassives.map { PassiveResponse(it.id, it.title, it.description) },
            tier4.map { TierResponse(it.id, it.title, it.description, it.time) },
        )
    }
}