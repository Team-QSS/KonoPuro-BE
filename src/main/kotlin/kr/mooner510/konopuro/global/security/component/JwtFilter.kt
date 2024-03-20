package kr.mooner510.konopuro.global.security.component

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.mooner510.konopuro.global.security.data.entity.AuthKey
import kr.mooner510.konopuro.global.utils.UUIDParser
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtFilter(
    private val tokenProvider: TokenProvider
) : OncePerRequestFilter() {

    companion object {
        const val AUTH = "Authorization"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authKey: AuthKey? = getToken(request)?.let { tokenProvider.checkAccessExpired(it) }
        authKey?.let {
            val user = tokenProvider.getUser(authKey)
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(user, it, user.authorities)
        }
        filterChain.doFilter(request, response)
    }

    private fun getToken(request: HttpServletRequest): AuthKey? {
        return tokenProvider.getAccessKey(UUIDParser.transfer(request.getHeader(AUTH) ?: return null))
    }
}