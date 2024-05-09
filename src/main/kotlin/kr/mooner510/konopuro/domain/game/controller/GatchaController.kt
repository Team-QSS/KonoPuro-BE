package kr.mooner510.konopuro.domain.game.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.transaction.Transactional
import kr.mooner510.konopuro.domain.game.component.GatchaManager
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponses
import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaStack
import kr.mooner510.konopuro.domain.game.data.gatcha.response.GatchaLogDataResponse
import kr.mooner510.konopuro.domain.game.data.gatcha.response.GatchaLogResponse
import kr.mooner510.konopuro.domain.game.data.gatcha.response.GatchaResponse
import kr.mooner510.konopuro.domain.game.data.gatcha.response.GatchaResponses
import kr.mooner510.konopuro.domain.game.exception.GatchaNotFoundException
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

@Tag(name = "Gatcha", description = "가챠 API")
@RestController
@RequestMapping("/api/gatcha")
class GatchaController(
    private val gatchaManager: GatchaManager,
    private val gatchaRepository: GatchaRepository,
    private val gatchaStackRepository: GatchaStackRepository, private val gatchaLogRepository: GatchaLogRepository,
    private val cardDataRepository: CardDataRepository,
) {

    @Operation(summary = "가챠 새로고침", description = "그냥 새로고침임 달라지는게 있을 지는 몰루?")
    @PostMapping
    fun updateGatcha() {
        thread {
            gatchaManager.update()
        }
    }

    @Operation(summary = "가챠 배너 목록 조회", description = "가챠할 수 있는 배너 목록을 반환합니다.")
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

    @Operation(summary = "가챠 1뽑", description = "가챠 베너 ID를 주면 해당 가챠를 1뽑 진행한 결과를 보여줍니다.")
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

    @Operation(summary = "가챠 10뽑", description = "가챠 베너 ID를 주면 해당 가챠를 10뽑 진행한 결과를 보여줍니다.")
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

    @Operation(summary = "가챠 기록 조회", description = "모든 가챠 기록을 조회합니다. 배너 구분은 없습니다.")
    @GetMapping("/log")
    fun gatchaLog(
        @AuthenticationPrincipal user: User,
        @RequestParam(required = false) tier: String?
    ): GatchaLogDataResponse {
        val stack = gatchaStackRepository.findByUserId(user.id).getOrNull() ?: return GatchaLogDataResponse(
            0, 0, 0, 0, false,
            full4 = false,
            data = emptyList()
        )

        val map = hashMapOf<Long, String>()

        var studentCardData: StudentCardData?
        val gatchaLogResponses =
            (tier?.let { gatchaLogRepository.findByUserIdAndTierIn(user.id, tier.split(",").map { it.toInt() }) }
                ?: gatchaLogRepository.findByUserId(user.id))
                .mapNotNull { log ->
                    map[log.cardDataId]?.let {
                        return@mapNotNull GatchaLogResponse(it, log.tier, log.stack, log.createdAt)
                    }
                    studentCardData = cardDataRepository.findByIdOrNull(log.cardDataId)
                    studentCardData?.let {
                        map[log.cardDataId] = it.title
                        return@mapNotNull GatchaLogResponse(it.title, log.tier, log.stack, log.createdAt)
                    }
                }
        return GatchaLogDataResponse(
            gatchaLogRepository.countByUserId(user.id),
            gatchaLogResponses.size,
            stack.stack3,
            stack.stack4,
            stack.full3,
            stack.full4,
            gatchaLogResponses
        )
    }
}