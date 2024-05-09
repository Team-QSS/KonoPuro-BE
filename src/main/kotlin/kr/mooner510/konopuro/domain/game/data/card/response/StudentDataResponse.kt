package kr.mooner510.konopuro.domain.game.data.card.response

import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType

data class StudentDataResponse(
    val id: String,
    val type: CardType,
    val cardGroups: List<MajorType>,
    val defaultPassives: List<PassiveType>,
    val tier1: TierType,
    val tier2: List<TierType>,
    val tier3: List<PassiveType>,
    val tier4: List<TierType>
)