package kr.mooner510.konopuro.domain.game.data.gatcha.response

import java.time.LocalDateTime

data class GatchaLogResponse(
    val cardId: String,
    val tier: Int,
    val stack: Int?,
    val dateTime: LocalDateTime
)