package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.repository.PassiveRepository
import kr.mooner510.konopuro.domain.game.repository.TierRepository
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.util.*

@Entity
@Table(name = "playercard")
class PlayerCard(
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    val userId: UUID,

    @Column(nullable = false, updatable = false)
    val cardDataId: Long,

    @Column(nullable = true)
    val tierSecond: Long?,

    @Column(nullable = true)
    val tierThird: Long?,

    @Column(nullable = true)
    val tierForth: Long?,
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

    fun split(cardData: CardData, passiveRepository: PassiveRepository, tierRepository: TierRepository): Pair<List<Passive>, List<Tier>> {
        val (passives, tiers) = when (cardData.type) {
            CardType.Student ->
                listOfNotNull(
                    cardData.passiveFirst,
                    cardData.passiveSecond,
                    cardData.passiveThird,
                    tierThird
                ) to listOfNotNull(
                    cardData.startTier,
                    tierSecond,
                    tierForth
                )

            else ->
                listOfNotNull(
                    cardData.passiveFirst,
                    cardData.passiveSecond,
                    cardData.passiveThird,
                    tierSecond,
                    tierThird,
                    tierForth
                ) to emptyList()
        }
        return passiveRepository.findAllById(passives).toList() to tierRepository.findAllById(tiers).toList()
    }
}