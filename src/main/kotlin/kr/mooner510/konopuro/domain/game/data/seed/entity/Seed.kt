package kr.mooner510.konopuro.domain.game.data.seed.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.util.*

@Entity
@Table(name = "seed")
class Seed(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val userId: UUID,

    @Column(nullable = false)
    var seed: Int
) : BaseEntity()
