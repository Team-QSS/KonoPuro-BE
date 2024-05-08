package kr.mooner510.konopuro.domain.game.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.transaction.Transactional
import kr.mooner510.konopuro.domain.game.data.card.entity.*
import kr.mooner510.konopuro.domain.game.data.card.request.CreateCardRequest
import kr.mooner510.konopuro.domain.game.data.card.request.PassiveRequest
import kr.mooner510.konopuro.domain.game.data.card.request.TierRequest
import kr.mooner510.konopuro.domain.game.data.card.response.CardDataResponse
import kr.mooner510.konopuro.domain.game.data.card.response.CardDataResponses
import kr.mooner510.konopuro.domain.game.data.card.response.PassiveResponse
import kr.mooner510.konopuro.domain.game.data.card.response.TierResponse
import kr.mooner510.konopuro.domain.game.exception.CardAlreadyExistsException
import kr.mooner510.konopuro.domain.game.exception.CardNotFoundException
import kr.mooner510.konopuro.domain.game.repository.*
import kr.mooner510.konopuro.domain.game.utils.PassiveTierUtils.toResponse
import kr.mooner510.konopuro.global.security.exception.InvalidParameterException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@Tag(name = "Card", description = "카드 API")
@RestController
@RequestMapping("/api/card")
class CardController(
    private val cardDataRepository: CardDataRepository,
    private val tierRepository: TierRepository,
    private val passiveRepository: PassiveRepository,
    private val tierMappingRepository: TierMappingRepository,
    private val passiveMappingRepository: PassiveMappingRepository
) {
    fun parseCardData(studentCardData: StudentCardData): CardDataResponse {
        val tiers = tierMappingRepository.findByCardDataId(studentCardData.id).groupBy({ it.tier }, { tierRepository.findByIdOrNull(it.tierId) })
        val passives = listOfNotNull(studentCardData.passiveFirst, studentCardData.passiveSecond, studentCardData.passiveThird)
        val passive = passiveMappingRepository.findByCardDataId(studentCardData.id).mapNotNull { passiveRepository.findByIdOrNull(it.passiveId) }

        return CardDataResponse(
            studentCardData.groupSet().toList(),
            studentCardData.type,
            passives.toResponse(),
            tiers[2]?.mapNotNull { tier -> tier?.let { TierResponse(it.id, it.title, it.description, it.time) } } ?: emptyList(),
            passive.toResponse(),
            tiers[4]?.mapNotNull { tier -> tier?.let { TierResponse(it.id, it.title, it.description, it.time) } } ?: emptyList(),
        )
    }

    @Operation(summary = "카드 데이터 조회", description = "내가 가지고 있든 말든 카드 정보를 조회합니다")
    @GetMapping
    fun getCardData(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) id: Long?
    ): CardDataResponse {
        val studentCardData: StudentCardData = (id?.let { cardDataRepository.findById(id) }
            ?: name?.let { cardDataRepository.findByTitleStartsWith(it) }
            ?: throw InvalidParameterException()).getOrNull()
            ?: throw CardNotFoundException()
        return parseCardData(studentCardData)
    }

    @Operation(summary = "모든 카드 데이터 조회", description = "내가 가지고 있든 말든 모든 카드 정보를 조회합니다")
    @GetMapping("/all")
    fun getAllCardData(): CardDataResponses {
        return CardDataResponses(cardDataRepository.findAll().map { parseCardData(it) })
    }

    @Hidden
    @PostMapping
    @Transactional
    fun createCardData(
        @RequestBody req: CreateCardRequest
    ): CardDataResponse {
        if (cardDataRepository.existsByTitle(req.title)) throw CardAlreadyExistsException()

        val tier2 = req.tier2.map {
            tierRepository.save(
                Tier(
                    it.title,
                    it.description,
                    it.time
                )
            )
        }

        val tier4 = req.tier4.map {
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

        val studentCardData = cardDataRepository.save(
            StudentCardData(
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

        tier2.map { tierMappingRepository.save(TierMapping(it.id, studentCardData.id, 2)) }
        tier4.map { tierMappingRepository.save(TierMapping(it.id, studentCardData.id, 4)) }
        additionPassives.map { passiveMappingRepository.save(PassiveMapping(it.id, studentCardData.id)) }

        return CardDataResponse(
            studentCardData.title,
            studentCardData.description,
            studentCardData.groupSet().toList(),
            studentCardData.type,
            defaultPassives.toResponse(),
            tier2.toResponse(),
            additionPassives.toResponse(),
            tier4.toResponse(),
        )
    }

    @Hidden
    @PostMapping("/passive")
    fun createPassive(
        @RequestBody req: PassiveRequest
    ): PassiveResponse {
        passiveRepository.findByTitle(req.title).getOrNull()?.let {
            return PassiveResponse(
                it.id,
                it.title,
                it.description
            )
        }

        val passive = passiveRepository.save(Passive(req.title, req.description))
        return PassiveResponse(
            passive.id,
            passive.title,
            passive.description
        )
    }

    @Hidden
    @PostMapping("/tier")
    fun createTier(
        @RequestBody req: TierRequest
    ): TierResponse {
        tierRepository.findByTitle(req.title).getOrNull()?.let {
            return TierResponse(
                it.id,
                it.title,
                it.description,
                it.time
            )
        }

        val tier = tierRepository.save(Tier(req.title, req.description, req.time))
        return TierResponse(
            tier.id,
            tier.title,
            tier.description,
            tier.time
        )
    }
}