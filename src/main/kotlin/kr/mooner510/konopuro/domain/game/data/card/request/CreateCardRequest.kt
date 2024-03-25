package kr.mooner510.konopuro.domain.game.data.card.request

import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType

data class CreateCardRequest(
    val title: String,
    val description: String,
    val cardGroups: List<MajorType>,
    val type: CardType,
    val passives: List<PassiveRequest>,
    val tiers: List<TierRequest>
)