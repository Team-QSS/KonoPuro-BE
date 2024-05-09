package kr.mooner510.konopuro.domain.game.data.card.response

import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import java.util.UUID

data class PlayerCardResponse(
    val id: UUID,
    val cardGroups: List<MajorType>,
    val tier: Int,
    val type: CardType,
    val passives: List<PassiveType>,
    val tiers: List<TierType>,
)