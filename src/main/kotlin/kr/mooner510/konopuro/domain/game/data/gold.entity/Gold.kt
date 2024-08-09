package kr.mooner510.konopuro.domain.game.data.gold.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "gold")

class Gold(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @Column(nullable = false)
    var gold: Int
) : BaseEntity()