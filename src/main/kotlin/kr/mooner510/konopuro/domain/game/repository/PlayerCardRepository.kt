package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.card.entity.PlayerCard
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PlayerCardRepository : CrudRepository<PlayerCard, UUID> {


    fun findByUserId(userId: UUID): List<PlayerCard>
}