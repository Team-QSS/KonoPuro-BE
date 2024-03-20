package kr.mooner510.konopuro.global.security.component

import jakarta.transaction.Transactional
import kr.mooner510.konopuro.global.security.data.entity.AuthKey
import kr.mooner510.konopuro.global.security.data.entity.User
import kr.mooner510.konopuro.global.security.exception.ExpiredTokenException
import kr.mooner510.konopuro.global.security.exception.InvalidTokenException
import kr.mooner510.konopuro.global.security.exception.UserNotFoundException
import kr.mooner510.konopuro.global.security.repository.AuthKeyRepository
import kr.mooner510.konopuro.global.security.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class TokenProvider(
    @Value("\${security.access}") private val accessTime: Long,
    private val authKeyRepository: AuthKeyRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun newOAuth(userId: UUID, delete: Boolean = true): AuthKey {
        val authKey = AuthKey(UUID.randomUUID(), userId)
//        if (delete) oAuthRepository.deleteByUserId(userId)
        return authKeyRepository.save(authKey)
    }

    fun getUser(auth: AuthKey): User {
        return userRepository.findByIdOrNull(auth.userId) ?: throw UserNotFoundException()
    }

    fun getAccessKey(token: UUID): AuthKey {
        return authKeyRepository.findByIdOrNull(token) ?: throw InvalidTokenException(token.toString())
    }

    fun checkAccessExpired(auth: AuthKey): AuthKey {
        if (LocalDateTime.now().isAfter(auth.createdAt.plusSeconds(accessTime)))
            throw ExpiredTokenException()
        return auth
    }
}