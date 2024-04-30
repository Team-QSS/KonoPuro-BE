package kr.mooner510.konopuro.domain.game.data.deck.response

import java.util.*

data class DeckResponse(
    val deckId: UUID,
    val deck: List<UUID>
)
