package kr.mooner510.konopuro.domain.game.data.gatcha.response

import java.time.LocalDateTime

data class GatchaLogResponse(
    val cardName: String,
    val tier: Int,
    val dateTime: LocalDateTime
)