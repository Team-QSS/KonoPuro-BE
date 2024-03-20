package kr.mooner510.konopuro.global.security.data.request

data class SignUpRequest(
    val id: String,
    val password: String,
    val name: String,
    val grade: Int,
    val clazz: Int,
    val number: Int,
)