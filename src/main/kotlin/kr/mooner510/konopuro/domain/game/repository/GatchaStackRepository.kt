package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaStack
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GatchaStackRepository : CrudRepository<GatchaStack, UUID> {
}