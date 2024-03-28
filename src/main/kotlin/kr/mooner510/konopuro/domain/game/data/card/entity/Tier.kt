package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.util.*

@Entity
@Table(name = "tier")
class Tier(
    @Column(nullable = false)
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