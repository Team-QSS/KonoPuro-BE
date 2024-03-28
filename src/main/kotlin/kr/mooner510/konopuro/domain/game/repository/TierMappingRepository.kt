package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.card.entity.TierMapping
import org.springframework.data.repository.CrudRepository

interface TierMappingRepository : CrudRepository<TierMapping, Long> {
    fun findByCardDataId(cardDataId: Long): List<TierMapping>
}