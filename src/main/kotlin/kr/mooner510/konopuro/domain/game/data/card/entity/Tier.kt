package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity

@Entity
@Table(
    name = "tier", indexes = [
        Index(name = "idx_tier_title_unq", columnList = "title", unique = true)
    ]
)
class Tier(
    @Column(nullable = false, unique = true)
    var title: String,

    @Column(nullable = false, length = 1023)
    var description: String,

    @Column(nullable = false)
    var time: Int
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}