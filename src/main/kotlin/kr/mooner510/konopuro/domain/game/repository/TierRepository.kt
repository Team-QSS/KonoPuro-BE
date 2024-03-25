package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.card.entity.Tier
import org.springframework.data.repository.CrudRepository

interface TierRepository : CrudRepository<Tier, Long> {
}