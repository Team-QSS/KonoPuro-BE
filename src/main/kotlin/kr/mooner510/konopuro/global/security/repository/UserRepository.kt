package kr.mooner510.konopuro.global.security.repository

import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {

    fun findByLoginId(loginId: String): Optional<User>

    fun existsByLoginId(loginId: String): Boolean


    @Transactional
    @Modifying
    @Query("update User u set u.client = ?1 where u.id = ?2")
    fun updateClientById(client: UUID?, id: UUID)


    @Transactional
    @Modifying
    @Query("update User u set u.client = null where u.client = ?1")
    fun updateClientToNull(client: UUID)
}
