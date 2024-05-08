package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.repository.PassiveRepository
import kr.mooner510.konopuro.domain.game.repository.TierRepository
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.util.*

@Entity
@Table(name = "player_card")
class PlayerCard(
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    val userId: UUID,

    @Column(nullable = false, updatable = false)
    val cardDataId: Long
) : BaseEntity() {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID()

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