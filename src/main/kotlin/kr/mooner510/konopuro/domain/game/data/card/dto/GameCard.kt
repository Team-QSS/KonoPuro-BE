package kr.mooner510.konopuro.domain.game.data.card.dto

import kr.mooner510.konopuro.domain.game.data.card.entity.CardData
import kr.mooner510.konopuro.domain.game.data.card.entity.PlayerCard
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import java.util.*

data class GameCard(
    val playerCardId: UUID,
    val name: String,
    val description: String,
    val groups: Set<MajorType>,
    val type: CardType,
    val startTier: Long?,
    val passiveFirst: Long,
    val passiveSecond: Long?,
    val passiveThird: Long?,

    val tierSecond: Long?,
    val tierThird: Long?,
    val tierForth: Long?,
) {
    companion object {
        fun new(playerCard: PlayerCard, cardData: CardData): GameCard = GameCard(
            playerCard.id,
            cardData.title,
            cardData.description,
            cardData.groupSet(),
            cardData.type,
            cardData.startTier,
            cardData.passiveFirst,
            cardData.passiveSecond,
            cardData.passiveThird,
            playerCard.tierSecond,
            playerCard.tierThird,
            playerCard.tierForth
        )
    }
}
