package kr.mooner510.konopuro.domain.game.data.deck.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "active_deck")
class ActiveDeck(
    @Id
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    val userId: UUID,

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    val deckId: UUID
)
