package kr.mooner510.konopuro.domain.game.data.card.dto

import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.StudentCardType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import java.util.*

data class GameCard(
    val playerCardId: UUID,
    val studentCardType: StudentCardType?,
    val defaultCardType: DefaultCardType?,
    val groups: Set<MajorType>,
    val type: CardType,
    val tiers: EnumSet<TierType>,
    val passives: EnumSet<PassiveType>
)
