package kr.mooner510.konopuro.global.security.data.request

data class IdChangeRequest(
    val id: String,
    val password: String,
    val newId: String,
)