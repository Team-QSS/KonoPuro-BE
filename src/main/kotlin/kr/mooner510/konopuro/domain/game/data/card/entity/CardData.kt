package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity

@Entity
@Table(
    name = "card_data", indexes = [
        Index(name = "idx_carddata_title", columnList = "title", unique = true)
    ]
)
class CardData(
    @Column(nullable = false, unique = true)
    val title: String,

    @Column(nullable = false, length = 1023)
    val description: String,

    @Column(nullable = false)
    val cardGroup: Long,

    @Column(nullable = false)
    val type: CardType,

    @Column(nullable = true)
    val startTier: Long?,

    @Column(nullable = false)
    val passiveFirst: Long,

    @Column(nullable = true)
    val passiveSecond: Long?,

    @Column(nullable = true)
    val passiveThird: Long?,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    fun groupSet(): Set<MajorType> {
        return MajorType.entries.filter {
            cardGroup and (1L shl it.ordinal) > 0
        }.toSet()
    }
}