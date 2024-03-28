package kr.mooner510.konopuro.domain.game.data.gatcha.response

import java.time.LocalDateTime
import java.util.*

data class GatchaResponse(
    val id: UUID,
    val name: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
)