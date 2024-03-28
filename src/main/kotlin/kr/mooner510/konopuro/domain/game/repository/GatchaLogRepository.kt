package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaLog
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GatchaLogRepository : CrudRepository<GatchaLog, Long> {


    fun findByUserId(userId: UUID): List<GatchaLog>
}