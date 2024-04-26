package kr.mooner510.konopuro.global.security.repository

import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {

    fun findByLoginId(loginId: String): Optional<User>

    fun existsByLoginId(loginId: String): Boolean
}