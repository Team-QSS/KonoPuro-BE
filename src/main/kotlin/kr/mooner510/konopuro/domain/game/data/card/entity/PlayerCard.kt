package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.StudentCardType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.dto.GameCard
import kr.mooner510.konopuro.domain.game.data.card.dto.GameStudentCard
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponse
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.util.*

@Entity
@Table(name = "player_card")
class PlayerCard(
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    val userId: UUID,

    @Column(nullable = false, columnDefinition = "VARCHAR(15)")
    val cardType: String,

    @Column(nullable = false)
    val isStudent: Boolean = false,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(15)")
    val tierSecond: TierType? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(15)")
    val tierThird: PassiveType? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(15)")
    val tierForth: TierType? = null,
) : BaseEntity() {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID()

    fun getTier(): Int {
        if (isStudent) {
            if (tierForth != null) return 4
            if (tierThird != null) return 3
            if (tierSecond != null) return 2
            return 1
        } else {
            return DefaultCardType.valueOf(cardType).tier
        }
    }

    fun toGameCard(): GameCard = GameCard(id, DefaultCardType.valueOf(cardType))

    fun toGameStudentCard(): GameStudentCard {
        val type = StudentCardType.valueOf(cardType)
        return GameStudentCard(
            id,
            type,
            type.major,
            EnumSet.copyOf(listOfNotNull(type.tier, tierSecond, tierForth)),
            EnumSet.copyOf(type.passive + setOfNotNull(tierThird)),
        )
    }

    fun toResponse(): PlayerCardResponse {
        if (isStudent) {
            val type = StudentCardType.valueOf(cardType)
            return PlayerCardResponse(
                id,
                type.toString(),
                type.major.toList(),
                getTier(),
                CardType.Student,
                (type.passive + type.thirdPassive).sortedBy { it.ordinal },
                (type.secondTier + type.forthTier + type.tier).sortedBy { it.ordinal },
            )
        } else {
            val type = DefaultCardType.valueOf(cardType)
            return PlayerCardResponse(
                id,
                type.toString(),
                emptyList(),
                getTier(),
                type.cardType,
                type.passives.toList(),
                emptyList()
            )
        }
    }
}
