package kr.mooner510.konopuro.domain.game.data.card.response

import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType

data class PlayerCardResponse(
    val title: String,
    val description: String,
    val cardGroups: List<MajorType>,
    val tier: Int,
    val type: CardType,
    val defaultPassives: List<PassiveResponse>,
    val passives: List<PassiveResponse>,
    val tiers: List<TierResponse>
)