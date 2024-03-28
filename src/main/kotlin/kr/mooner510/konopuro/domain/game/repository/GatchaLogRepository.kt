package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.gatcha.entity.GatchaLog
import org.springframework.data.repository.CrudRepository

interface GatchaLogRepository : CrudRepository<GatchaLog, Long> {
}