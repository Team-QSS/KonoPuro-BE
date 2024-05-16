package kr.mooner510.konopuro.domain.game._preset

import kr.mooner510.konopuro.domain.game.data.card.response.DefaultDataResponse
import kr.mooner510.konopuro.domain.game.data.card.types.CardType

enum class DefaultCardType(
    val tier: Int,
    val cardType: CardType
) {
    OnlyPower(1, CardType.Field),
    UltimatePower(1, CardType.Field)
    ;

    companion object {
        val tierOtherList = DefaultCardType.entries.filter { it.tier != 3 }
        val tier3List = DefaultCardType.entries.filter { it.tier == 3 }
    }

    fun toResponse(): DefaultDataResponse {
        return DefaultDataResponse(
            toString(),
            cardType,
            tier
        )
    }
}