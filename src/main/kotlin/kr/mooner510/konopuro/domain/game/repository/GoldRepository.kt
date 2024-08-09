package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.gold.entity.Gold
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface GoldRepository : CrudRepository<Gold, UUID> {
    @Transactional
    @Modifying
    @Query("update Gold g set g.gold = ?1 where g.id = ?2")
    fun updateGoldById(gold: Int, id: UUID)
}