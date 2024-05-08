package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity

@Entity
@Table(
    name = "student_card_data", indexes = [
        Index(name = "idx_student_carddata_title", columnList = "title", unique = true)
    ]
)
class StudentCardData(
    @Column(nullable = false)
    val cardGroup: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    val type: CardType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(15)")
    val startTier: TierType?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    val passiveFirst: PassiveType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(15)")
    val passiveSecond: PassiveType?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(15)")
    val passiveThird: PassiveType?,
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