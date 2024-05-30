package kr.mooner510.konopuro.domain.socket.data

open class RawProtocol(
    val protocol: Int,
    val data: List<Any>
) {
    operator fun get(i: Int) = data[i]

    constructor(proto: Int, vararg dataArg: Any) : this(proto, listOf(*dataArg))
}