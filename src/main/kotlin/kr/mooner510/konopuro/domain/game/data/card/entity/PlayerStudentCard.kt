package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.StudentCardType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.repository.PassiveRepository
import kr.mooner510.konopuro.domain.game.repository.TierRepository
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.util.*

@Entity
@Table(name = "player_student_card")
class PlayerStudentCard(
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    val studentCardType: StudentCardType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(15)")
    val tierSecond: TierType?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(15)")
    val tierThird: PassiveType?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(15)")
    val tierForth: TierType?,
) : BaseEntity() {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID()

    fun getTier(): Int {
        if (tierForth != null) return 4
        if (tierThird != null) return 3
        if (tierSecond != null) return 2
        return 1
    }

    fun split(
        studentCardData: StudentCardData,
        passiveRepository: PassiveRepository,
        tierRepository: TierRepository
    ): Pair<List<PassiveType>, List<TierType>> {
        return when (studentCardData.type) {
            CardType.Student ->
                listOfNotNull(
                    studentCardData.passiveFirst,
                    studentCardData.passiveSecond,
                    studentCardData.passiveThird,
                    tierThird
                ) to listOfNotNull(
                    studentCardData.startTier,
                    tierSecond,
                    tierForth
                )

            else ->
                listOfNotNull(
                    studentCardData.passiveFirst,
                    studentCardData.passiveSecond,
                    studentCardData.passiveThird,
                    tierSecond,
                    tierThird,
                    tierForth
                ) to emptyList()
        }
    }
}