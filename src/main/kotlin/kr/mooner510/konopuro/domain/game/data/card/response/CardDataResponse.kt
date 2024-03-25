package kr.mooner510.konopuro.domain.game.data.card.response

import kr.mooner510.konopuro.domain.game.data.card.request.PassiveRequest
import kr.mooner510.konopuro.domain.game.data.card.request.TierRequest
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType

data class CardDataResponse(
    val title: String,
    val description: String,
    val cardGroups: List<MajorType>,
    val type: CardType,
    val passives: List<PassiveResponse>,
    val tiers: List<TierResponse>
)