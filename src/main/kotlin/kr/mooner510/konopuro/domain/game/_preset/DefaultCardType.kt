package kr.mooner510.konopuro.domain.game._preset

import kr.mooner510.konopuro.domain.game.data.card.response.DefaultDataResponse
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import java.util.*

enum class DefaultCardType(
    val tier: Int,
    val time: Int,
    val cardType: CardType,
    val passives: EnumSet<PassiveType> = EnumSet.noneOf(PassiveType::class.java)
) {
    OnlyPower(1, 0, CardType.Field),
    UltimatePower(1, 0, CardType.Field),
    Music(1, 0, CardType.Field),
    DawnCoding(1, 0, CardType.Field),
    Passion(1, 0, CardType.Field),
    Test(1, 5, CardType.Tool, EnumSet.of(PassiveType.Test))
    ;

    companion object {
        val tierOtherList = DefaultCardType.entries.filter { it.tier != 3 && it.cardType != CardType.Field }
        val tier3List = DefaultCardType.entries.filter { it.tier == 3 && it.cardType != CardType.Field }
    }

    fun toResponse(): DefaultDataResponse {
        return DefaultDataResponse(
            toString(),
            cardType,
            tier
        )
    }
}
