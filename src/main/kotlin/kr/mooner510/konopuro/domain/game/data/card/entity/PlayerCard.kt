package kr.mooner510.konopuro.domain.game.data.card.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.util.*

@Entity
@Table(name = "card")
class PlayerCard(
    @Column(nullable = false, updatable = false)
    val cardDataId: Long,

    @Column(nullable = true)
    val tierSecond: Long?,

    @Column(nullable = true)
    val tierThird: Long?,

    @Column(nullable = true)
    val tierForth: Long?
) : BaseEntity() {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID()
}