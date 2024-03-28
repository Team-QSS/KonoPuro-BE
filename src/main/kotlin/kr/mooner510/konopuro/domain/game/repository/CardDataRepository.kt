package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.card.entity.CardData
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CardDataRepository : CrudRepository<CardData, Long> {
    fun findByTitleStartsWith(title: String): Optional<CardData>


    fun existsByTitle(title: String): Boolean
}