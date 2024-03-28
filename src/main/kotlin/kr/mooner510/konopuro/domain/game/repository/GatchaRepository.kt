package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.gatcha.entity.Gatcha
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GatchaRepository : CrudRepository<Gatcha, UUID> {


    fun existsByTitle(title: String): Boolean
}