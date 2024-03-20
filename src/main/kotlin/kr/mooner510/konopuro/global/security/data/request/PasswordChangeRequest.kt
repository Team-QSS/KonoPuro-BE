package kr.mooner510.konopuro.global.security.data.request

data class PasswordChangeRequest(
    val id: String,
    val password: String,
    val newPassword: String,
)