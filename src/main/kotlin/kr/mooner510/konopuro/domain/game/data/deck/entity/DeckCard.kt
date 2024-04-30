package kr.mooner510.konopuro.domain.game.data.deck.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "deck_card")
class DeckCard(
    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    val cardId: UUID,

    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    val deckId: UUID
)