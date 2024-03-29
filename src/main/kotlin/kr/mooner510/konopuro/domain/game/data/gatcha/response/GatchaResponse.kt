package kr.mooner510.konopuro.domain.game.data.gatcha.response

import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import java.time.LocalDateTime
import java.util.*

data class GatchaResponse(
    val id: UUID,
    val name: String,
    val major: MajorType,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
)