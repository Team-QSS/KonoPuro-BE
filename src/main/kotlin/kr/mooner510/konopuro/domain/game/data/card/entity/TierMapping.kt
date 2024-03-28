package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import kr.mooner510.konopuro.global.global.data.entity.BaseEntityCreateOnly
import java.util.*

@Entity
@Table(name = "tier")
class TierMapping(
    @Column(nullable = false, updatable = false)
    val tierId: Long,

    @Column(nullable = false, updatable = false)
    val cardDataId: Long,

    @Column(nullable = false, updatable = false)
    val tier: Int,
) : BaseEntityCreateOnly() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}