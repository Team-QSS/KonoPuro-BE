package kr.mooner510.konopuro.domain.game.data.card.dto

import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import java.util.*

data class GameCard(
    val id: UUID,
    val defaultCardType: DefaultCardType,
    var limit: Int = 0,
    val dayTime: Boolean = false
)
