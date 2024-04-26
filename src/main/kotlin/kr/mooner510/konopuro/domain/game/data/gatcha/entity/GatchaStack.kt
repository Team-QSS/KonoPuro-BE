package kr.mooner510.konopuro.domain.game.data.gatcha.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import java.util.*

@Entity
@Table(name = "gatcha_stack")
class GatchaStack(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val userId: UUID,

    @Column(nullable = false)
    var stack4: Int,

    @Column(nullable = false)
    var full4: Boolean,

    @Column(nullable = false)
    var stack3: Int,

    @Column(nullable = false)
    var full3: Boolean,
) : BaseEntity() {
    fun chance4(): Double = if (stack4 <= 65) .006 else .006 + (stack4 - 65) / 15

    fun chance3(): Double = if (stack3 >= 10) 1.0 else if (stack3 >= 9) .35 else .04
}