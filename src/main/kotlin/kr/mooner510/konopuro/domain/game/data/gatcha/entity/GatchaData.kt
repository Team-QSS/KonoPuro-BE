package kr.mooner510.konopuro.domain.game.data.gatcha.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.global.global.data.entity.BaseEntityCreateOnly
import java.util.*

@Entity
@Table(name = "gatcha_log")
class GatchaData(
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    val userId: UUID,

    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    val cardDataId: UUID,

    @Column(nullable = false, updatable = false)
    val tier: Int,
) : BaseEntityCreateOnly() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    val id: Long = 0L
}