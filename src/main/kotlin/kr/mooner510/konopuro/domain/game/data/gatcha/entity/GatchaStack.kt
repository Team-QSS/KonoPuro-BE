package kr.mooner510.konopuro.domain.game.data.gatcha.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Entity
@Table(name = "gatcha_stack")
class GatchaStack(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val userId: UUID,

    @Column(nullable = false)
    var stack4: Int,

    @Column(nullable = false)
    var stack3: Int,
) : BaseEntity() {
    fun additionChance4(): Double = max(0.0, min(1.0, (stack4 - 65) / 40.0))

    fun additionChance3(): Double = if (stack3 == 9) .21 else 1.0
}