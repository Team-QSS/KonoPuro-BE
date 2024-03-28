package kr.mooner510.konopuro.domain.game.data.gatcha.entity

import jakarta.persistence.*
import kr.mooner510.konopuro.domain.game.data.card.entity.PlayerCard
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.global.global.data.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.util.UUID

@SQLRestriction(value = "is_deleted = false")
@SQLDelete(sql = "UPDATE user SET is_deleted = true where id = ?")
@Entity
@Table(name = "gatcha")
class Gatcha(
    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val mainMajor: MajorType,
) : BaseEntity() {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID()

    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean = false
}