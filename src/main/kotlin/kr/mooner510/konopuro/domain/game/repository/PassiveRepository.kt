package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.card.entity.Passive
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PassiveRepository : CrudRepository<Passive, Long> {


    fun findByTitle(title: String): Optional<Passive>
}