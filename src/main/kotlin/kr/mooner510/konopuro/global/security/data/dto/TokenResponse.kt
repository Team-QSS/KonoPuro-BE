package kr.mooner510.konopuro.global.security.data.dto

import kr.mooner510.konopuro.global.security.component.TokenProvider
import java.util.*

data class TokenResponse(
    val accessToken: String
) {
    companion object {
        fun new(tokenProvider: TokenProvider, id: UUID): TokenResponse {
            val oAuth = tokenProvider.newOAuth(id)
            return TokenResponse(oAuth.accessToken.toString())
        }
    }
}
