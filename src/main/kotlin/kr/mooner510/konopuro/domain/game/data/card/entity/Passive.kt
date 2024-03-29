package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity

@Entity
@Table(
    name = "passive", indexes = [
        Index(name = "idx_passive_title", columnList = "title", unique = true)
    ]
)
class Passive(
    @Column(nullable = false, unique = true)
    var title: String,

    @Column(nullable = false, length = 1023)
    var description: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}