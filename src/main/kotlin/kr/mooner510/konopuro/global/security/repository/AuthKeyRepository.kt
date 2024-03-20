package kr.mooner510.konopuro.global.security.repository

import kr.mooner510.konopuro.global.security.data.entity.AuthKey
import org.springframework.data.repository.CrudRepository
import java.util.*

interface AuthKeyRepository : CrudRepository<AuthKey, UUID> {
    fun findByUserId(userId: UUID): Optional<AuthKey>


    fun deleteByUserId(userId: UUID)
}