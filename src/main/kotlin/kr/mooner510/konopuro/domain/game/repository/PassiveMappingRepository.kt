package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.card.entity.PassiveMapping
import org.springframework.data.repository.CrudRepository

interface PassiveMappingRepository : CrudRepository<PassiveMapping, Long> {


    fun findByCardDataId(cardDataId: Long): List<PassiveMapping>
}