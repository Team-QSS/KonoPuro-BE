package kr.mooner510.konopuro.global.security.controller

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.transaction.Transactional
import kr.mooner510.konopuro.global.security.component.TokenProvider
import kr.mooner510.konopuro.global.security.data.dto.TokenResponse
import kr.mooner510.konopuro.global.security.data.entity.User
import kr.mooner510.konopuro.global.security.data.request.IdChangeRequest
import kr.mooner510.konopuro.global.security.data.request.PasswordChangeRequest
import kr.mooner510.konopuro.global.security.data.request.SignInRequest
import kr.mooner510.konopuro.global.security.data.request.SignUpRequest
import kr.mooner510.konopuro.global.security.exception.LoginFailedException
import kr.mooner510.konopuro.global.security.exception.UserIdAlreadyExistsException
import kr.mooner510.konopuro.global.security.exception.UserNotFoundException
import kr.mooner510.konopuro.global.security.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

@Tag(name = "Auth", description = "인증 및 계정 API")
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val tokenProvider: TokenProvider,
) {
    @PostMapping("/sign-in")
    fun signIn(@RequestBody req: SignInRequest): TokenResponse {
        val user = userRepository.findByLoginId(req.id).getOrNull() ?: throw LoginFailedException()

        if (passwordEncoder.matches(req.password, user.password)) {
            return TokenResponse.new(tokenProvider, user.id)
        }
        throw LoginFailedException()
    }

    @PostMapping("/id")
    @Transactional
    fun changeId(
        @RequestBody req: IdChangeRequest
    ) {
        val userData = userRepository.findByLoginId(req.id).getOrNull() ?: throw UserNotFoundException()
        if (userRepository.existsByLoginId(req.newId)) throw UserIdAlreadyExistsException()
        if (!passwordEncoder.matches(req.password, userData.password)) throw LoginFailedException()
        userData.loginId = req.id
    }

    @PostMapping("/password")
    @Transactional
    fun changePassword(
        @RequestBody req: PasswordChangeRequest
    ) {
        val userData = userRepository.findByLoginId(req.id).orElse(null) ?: throw UserNotFoundException()
        if (!passwordEncoder.matches(req.password, userData.password)) throw LoginFailedException()
        userData.password = passwordEncoder.encode(req.newPassword)
    }

    @PostMapping("/sign-up")
    fun signUp(@RequestBody req: SignUpRequest) {
        if (userRepository.existsByLoginId(req.id)) return
        userRepository.save(
            User(
                req.name,
                req.id,
                passwordEncoder.encode(req.password)
            )
        )
    }
}