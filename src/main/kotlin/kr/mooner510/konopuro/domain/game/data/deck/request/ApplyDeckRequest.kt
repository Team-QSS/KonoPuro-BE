package kr.mooner510.konopuro.domain.game.data.deck.request

import kr.mooner510.konopuro.domain.game.data.deck.response.DeckResponse
import java.util.UUID

data class ApplyDeckRequest(
    val activeDeckId: UUID,
    val addition: List<UUID>,
    val deletion: List<UUID>,
)
