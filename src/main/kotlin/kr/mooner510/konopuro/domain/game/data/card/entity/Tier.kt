package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.util.*

@Entity
@Table(name = "tier")
class Tier(
    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, length = 1023)
    val description: String,

    @Column(nullable = false)
    val time: Int,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}