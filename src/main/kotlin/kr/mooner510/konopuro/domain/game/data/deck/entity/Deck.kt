package kr.mooner510.konopuro.domain.game.data.deck.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "deck")
class Deck(
    val title: String,

    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    val userId: UUID
) {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID()
}