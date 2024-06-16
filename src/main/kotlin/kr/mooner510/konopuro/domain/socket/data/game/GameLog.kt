package kr.mooner510.konopuro.domain.socket.data.game

import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import java.util.*

data class GameLog(
    val id: UUID,
    val player: UUID,
    val isStudent: Boolean,
    val majorType: MajorType,
    val amount: Int
)
