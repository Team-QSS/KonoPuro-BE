package kr.mooner510.konopuro.domain.game.data.deck.request

import java.util.UUID

data class DeckCardRequest(
    val deckId: UUID,
    val cardId: UUID
)
