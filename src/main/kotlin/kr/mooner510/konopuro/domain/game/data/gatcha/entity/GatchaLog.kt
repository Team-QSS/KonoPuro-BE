package kr.mooner510.konopuro.domain.game.data.gatcha.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.global.global.data.entity.BaseEntityCreateOnly
import java.util.*

@Entity
@Table(name = "gatcha_log")
class GatchaLog(
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    val userId: UUID,

    @Column(nullable = false, updatable = false, length = 16)
    val cardType: String,

    @Column(nullable = false, updatable = false)
    val isStudent: Boolean,

    @Column(nullable = false, updatable = false)
    val tier: Int,

    @Column(nullable = true, updatable = false)
    val stack: Int?,
) : BaseEntityCreateOnly() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    val id: Long = 0L
}