package kr.mooner510.konopuro.domain.game.controller

import jakarta.transaction.Transactional
import kr.mooner510.konopuro.domain.game.component.GatchaManager
import kr.mooner510.konopuro.domain.game.data.card.entity.*
import kr.mooner510.konopuro.domain.game.data.card.request.CreateCardRequest
import kr.mooner510.konopuro.domain.game.data.card.response.CardDataResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PassiveResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponse
import kr.mooner510.konopuro.domain.game.data.card.response.TierResponse
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaStack
import kr.mooner510.konopuro.domain.game.exception.GatchaNotFoundException
import kr.mooner510.konopuro.domain.game.repository.*
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.pow

@RestController
@RequestMapping("/api/gatcha")
class GatchaController(
    private val gatchaManager: GatchaManager,
    private val cardDataRepository: CardDataRepository,
    private val tierRepository: TierRepository,
    private val passiveRepository: PassiveRepository,
    private val gatchaRepository: GatchaRepository,
    private val gatchaStackRepository: GatchaStackRepository,
    private val tierMappingRepository: TierMappingRepository,
    private val passiveMappingRepository: PassiveMappingRepository
) {

    @PostMapping
    fun updateGatcha() {
        thread {
            gatchaManager.update()
        }
    }

    @GetMapping("/once")
    @Transactional
    fun gatchaOnce(
        @AuthenticationPrincipal user: User,
        @RequestParam gatchaId: UUID
    ): PlayerCardResponse {
        val gatcha = gatchaRepository.findByIdOrNull(gatchaId) ?: throw GatchaNotFoundException()
        val stack = gatchaStackRepository.findByIdOrNull(user.id) ?: gatchaStackRepository.save(GatchaStack(user.id, 0, 0))

        return gatchaManager.gatcha(gatcha, stack)
    }

    @GetMapping("/multi")
    @Transactional
    fun gatchaMulti(
        @AuthenticationPrincipal user: User,
        @RequestParam gatchaId: UUID
    ): List<PlayerCardResponse> {
        val gatcha = gatchaRepository.findByIdOrNull(gatchaId) ?: throw GatchaNotFoundException()
        val stack = gatchaStackRepository.findByIdOrNull(user.id) ?: gatchaStackRepository.save(GatchaStack(user.id, 0, 0))

        return Array(10) { it }.map { gatchaManager.gatcha(gatcha, stack) }.toList()
    }

    @PostMapping
    @Transactional
    fun createCardData(
        @RequestBody req: CreateCardRequest
    ): CardDataResponse {
        val tiers = req.tiers.map { tierRequests ->
            tierRequests.map {
                tierRepository.save(
                    Tier(
                        it.title,
                        it.description,
                        it.time
                    )
                )
            }
        }

        val defaultPassives = req.defaultPassives.map {
            passiveRepository.save(
                Passive(
                    it.title,
                    it.description
                )
            )
        }

        val passive = passiveRepository.save(Passive(req.additionPassive.title, req.additionPassive.description))

        val cardData = cardDataRepository.save(
            CardData(
                req.title,
                req.description,
                req.cardGroups.sumOf { 2.0.pow(it.ordinal) }.toLong(),
                req.type,
                req.startTierId,
                defaultPassives[0].id,
                if (defaultPassives.size > 1) defaultPassives[1].id else null,
                if (defaultPassives.size > 2) defaultPassives[2].id else null
            )
        )

        tiers.mapIndexed { idx, tierList ->
            tierList.map {
                tierMappingRepository.save(TierMapping(it.id, cardData.id, (idx + 1) * 2))
            }
        }
        passiveMappingRepository.save(PassiveMapping(passive.id, cardData.id))

        return CardDataResponse(
            cardData.title,
            cardData.description,
            cardData.groupSet().toList(),
            cardData.type,
            defaultPassives.map { PassiveResponse(it.id, it.title, it.description) },
            tiers[0].map { TierResponse(it.id, it.title, it.description, it.time) },
            tiers[1].map { TierResponse(it.id, it.title, it.description, it.time) },
            PassiveResponse(passive.id, passive.title, passive.description),
            tiers[2].map { TierResponse(it.id, it.title, it.description, it.time) },
        )
    }
}