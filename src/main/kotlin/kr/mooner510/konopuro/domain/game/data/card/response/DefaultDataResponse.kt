package kr.mooner510.konopuro.domain.game.data.card.response

import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType

data class DefaultDataResponse(
    val id: String,
    val type: CardType,
    val tier: Int
)