package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.card.entity.Passive
import org.springframework.data.repository.CrudRepository

interface PassiveRepository : CrudRepository<Passive, Long> {
}