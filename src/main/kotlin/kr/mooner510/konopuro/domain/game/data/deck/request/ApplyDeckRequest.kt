package kr.mooner510.konopuro.domain.game.data.deck.request

import java.util.*

data class ApplyDeckRequest(
    val activeDeckId: UUID,
    val addition: List<UUID>,
    val deletion: List<UUID>,
)
