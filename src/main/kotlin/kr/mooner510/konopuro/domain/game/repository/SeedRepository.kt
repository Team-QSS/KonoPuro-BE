package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.seed.entity.Seed
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface SeedRepository : CrudRepository<Seed, UUID> {


    @Transactional
    @Modifying
    @Query("update Seed s set s.seed = ?1 where s.userId = ?2")
    fun updateSeedByUserId(seed: Int, userId: UUID)
}