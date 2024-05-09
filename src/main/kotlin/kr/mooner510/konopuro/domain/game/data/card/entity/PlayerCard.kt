package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.StudentCardType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.dto.GameCard
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

    fun toGameCard(): GameCard {
        if (isStudent) {
            val type = StudentCardType.valueOf(cardType)
            return GameCard(
                id,
                type,
                null,
                type.major,
                CardType.Student,
                EnumSet.copyOf(listOfNotNull(type.tier, tierSecond, tierForth)),
                EnumSet.copyOf(type.passive + setOfNotNull(tierThird)),
            )
        } else {
            val type = DefaultCardType.valueOf(cardType)
            return GameCard(
                id,
                null,
                type,
                emptySet(),
                CardType.Student,
                EnumSet.noneOf(TierType::class.java),
                EnumSet.noneOf(PassiveType::class.java),
            )
        }
    }

    fun toResponse(): PlayerCardResponse {
        if (isStudent) {
            val type = StudentCardType.valueOf(cardType)
            return PlayerCardResponse(
                id,
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
                emptyList(),
                getTier(),
                type.cardType,
                emptyList(),
                emptyList()
            )
        }
    }
}