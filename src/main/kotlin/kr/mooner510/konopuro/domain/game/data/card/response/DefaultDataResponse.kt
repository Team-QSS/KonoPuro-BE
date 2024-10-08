package kr.mooner510.konopuro.domain.game.data.card.response

import kr.mooner510.konopuro.domain.game.data.card.types.CardType

data class DefaultDataResponse(
    val id: String,
    val type: CardType,
    val tier: Int
)
