package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*

@Entity
@Table(name = "card_inventory")
class CardInventory(
    val playerCardId: Long,

    val userId: Long
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}