package kr.mooner510.konopuro.domain.game._preset

import kr.mooner510.konopuro.domain.game.data.card.response.DefaultDataResponse
import kr.mooner510.konopuro.domain.game.data.card.types.CardType

enum class DefaultCardType(
    val tier: Int,
    val time: Int,
    val cardType: CardType
) {
    OnlyPower(1, 0, CardType.Field),
    UltimatePower(1, 0, CardType.Field),
    Music(1, 0, CardType.Field),
    Mouse(2, 0, CardType.Tool),
    Keyboard(3, 0, CardType.Tool),
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