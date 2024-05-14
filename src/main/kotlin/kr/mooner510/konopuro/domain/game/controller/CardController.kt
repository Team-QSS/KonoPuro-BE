package kr.mooner510.konopuro.domain.game.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.StudentCardType
import kr.mooner510.konopuro.domain.game.data.card.response.CardDataResponses
import kr.mooner510.konopuro.domain.game.data.card.response.DefaultDataResponse
import kr.mooner510.konopuro.domain.game.data.card.response.StudentDataResponse
import kr.mooner510.konopuro.domain.socket.exception.CardDataNotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Card", description = "카드 API")
@RestController
@RequestMapping("/api/card")
class CardController {
    @Operation(summary = "학생 카드 데이터 조회", description = "내가 가지고 있든 말든 학생 카드 정보를 조회합니다")
    @GetMapping("/student")
    fun getStudentCardData(
        @RequestParam id: String
    ): StudentDataResponse {
        try {
            return StudentCardType.valueOf(id).toResponse()
        } catch (_: Exception) {
            throw CardDataNotFoundException()
        }
    }

    @Operation(summary = "일반 카드 데이터 조회", description = "내가 가지고 있든 말든 일반 카드 정보를 조회합니다")
    @GetMapping
    fun getCardData(
        @RequestParam id: String
    ): DefaultDataResponse {
        try {
            return DefaultCardType.valueOf(id).toResponse()
        } catch (_: Exception) {
            throw CardDataNotFoundException()
        }
    }

    @Operation(summary = "모든 카드 데이터 조회", description = "내가 가지고 있든 말든 모든 카드 정보를 조회합니다")
    @GetMapping("/all")
    fun getAllCardData(): CardDataResponses {
        return CardDataResponses(
            StudentCardType.entries.map { it.toResponse() },
            DefaultCardType.entries.map { it.toResponse() }
        )
    }
}