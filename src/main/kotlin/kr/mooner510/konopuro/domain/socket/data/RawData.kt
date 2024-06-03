package kr.mooner510.konopuro.domain.socket.data

open class RawData(
    val self: List<Any>? = null,
    val other: List<Any>? = null,
    val isTurn: Boolean
)