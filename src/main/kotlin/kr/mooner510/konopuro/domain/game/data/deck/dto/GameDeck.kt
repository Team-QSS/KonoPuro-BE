package kr.mooner510.konopuro.domain.game.data.deck.dto

import kr.mooner510.konopuro.domain.game.data.deck.entity.DeckCard
import java.util.UUID

data class GameDeck(
    val userId: UUID,
    val title: String,
    val cards: List<DeckCard>
)
