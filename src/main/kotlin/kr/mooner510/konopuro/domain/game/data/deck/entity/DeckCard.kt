package kr.mooner510.konopuro.domain.game.data.deck.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "deck_card")
class DeckCard(
    @Column(nullable = false, updatable = false)
    val cardIdx: Long,

    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    val playerCardId: UUID,

    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    val deckId: UUID
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}