package kr.mooner510.konopuro.domain.game.data.gatcha.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime
import java.util.*

@SQLRestriction(value = "is_deleted = false")
@SQLDelete(sql = "UPDATE gatcha SET is_deleted = true where id = ?")
@Entity
@Table(name = "gatcha")
class Gatcha(
    @Column(nullable = false)
    val title: String,

    @Column(nullable = true)
    val mainMajor: MajorType?,

    @Column(nullable = false)
    val startAt: LocalDateTime,

    @Column(nullable = false)
    val endAt: LocalDateTime,
) : BaseEntity() {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID()

    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean = false
}