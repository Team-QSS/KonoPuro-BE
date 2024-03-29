package kr.mooner510.konopuro.domain.game.controller

import jakarta.transaction.Transactional
import kr.mooner510.konopuro.domain.game.component.GatchaManager
import kr.mooner510.konopuro.domain.game.data.card.entity.CardData
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponses
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaStack
import kr.mooner510.konopuro.domain.game.data.gatcha.response.GatchaLogDataResponse
import kr.mooner510.konopuro.domain.game.data.gatcha.response.GatchaLogResponse
import kr.mooner510.konopuro.domain.game.data.gatcha.response.GatchaResponse
import kr.mooner510.konopuro.domain.game.data.gatcha.response.GatchaResponses
import kr.mooner510.konopuro.domain.game.exception.GatchaNotFoundException
import kr.mooner510.konopuro.domain.game.repository.CardDataRepository
import kr.mooner510.konopuro.domain.game.repository.GatchaLogRepository
import kr.mooner510.konopuro.domain.game.repository.GatchaRepository
import kr.mooner510.konopuro.domain.game.repository.GatchaStackRepository
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.concurrent.thread
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/gatcha")
class GatchaController(
    private val gatchaManager: GatchaManager,
    private val gatchaRepository: GatchaRepository,
    private val gatchaStackRepository: GatchaStackRepository, private val gatchaLogRepository: GatchaLogRepository,
    private val cardDataRepository: CardDataRepository,
) {

    @PostMapping
    fun updateGatcha() {
        thread {
            gatchaManager.update()
        }
    }

    @GetMapping("/list")
    fun gatchaList(): GatchaResponses {
        return GatchaResponses(
            gatchaRepository.findAll().map {
                GatchaResponse(
                    it.id,
                    it.title,
                    it.mainMajor,
                    it.startAt,
                    it.endAt
                )
            }
        )
    }

    @GetMapping("/once")
    @Transactional
    fun gatchaOnce(
        @AuthenticationPrincipal user: User,
        @RequestParam gatchaId: UUID
    ): PlayerCardResponse {
        val gatcha = gatchaRepository.findByIdOrNull(gatchaId) ?: throw GatchaNotFoundException()
        val stack = gatchaStackRepository.findByIdOrNull(user.id) ?: gatchaStackRepository.save(GatchaStack(user.id, 0, false, 0, false))

        return gatchaManager.gatcha(gatcha, stack)
    }

    @GetMapping("/multi")
    @Transactional
    fun gatchaMulti(
        @AuthenticationPrincipal user: User,
        @RequestParam gatchaId: UUID
    ): PlayerCardResponses {
        val gatcha = gatchaRepository.findByIdOrNull(gatchaId) ?: throw GatchaNotFoundException()
        val stack = gatchaStackRepository.findByIdOrNull(user.id) ?: gatchaStackRepository.save(GatchaStack(user.id, 0, false, 0, false))

        return PlayerCardResponses(Array(10) { it }.map { gatchaManager.gatcha(gatcha, stack) }.toList())
    }

    @GetMapping("/log")
    fun gatchaLog(
        @AuthenticationPrincipal user: User,
        @RequestParam(required = false) tier: String?
    ): GatchaLogDataResponse {
        val stack = gatchaStackRepository.findByUserId(user.id).getOrNull() ?: return GatchaLogDataResponse(0, 0, 0, emptyList())

        val map = hashMapOf<Long, String>()

        var cardData: CardData?
        val gatchaLogResponses =
            (tier?.let { gatchaLogRepository.findByUserIdAndTierIn(user.id, tier.split(",").map { it.toInt() }) }
                ?: gatchaLogRepository.findByUserId(user.id))
                .mapNotNull { log ->
                    map[log.cardDataId]?.let {
                        return@mapNotNull GatchaLogResponse(it, log.tier, log.stack, log.createdAt)
                    }
                    cardData = cardDataRepository.findByIdOrNull(log.cardDataId)
                    cardData?.let {
                        map[log.cardDataId] = it.title
                        return@mapNotNull GatchaLogResponse(it.title, log.tier, log.stack, log.createdAt)
                    }
                }
        return GatchaLogDataResponse(
            gatchaLogResponses.size,
            stack.stack3,
            stack.stack4,
            gatchaLogResponses
        )
    }
}