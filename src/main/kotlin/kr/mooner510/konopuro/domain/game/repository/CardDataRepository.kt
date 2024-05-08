package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.card.entity.StudentCardData
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CardDataRepository : CrudRepository<StudentCardData, Long> {
    fun findByTitleStartsWith(title: String): Optional<StudentCardData>


    fun existsByTitle(title: String): Boolean
}