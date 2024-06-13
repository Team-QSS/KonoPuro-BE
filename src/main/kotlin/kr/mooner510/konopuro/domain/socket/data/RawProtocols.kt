package kr.mooner510.konopuro.domain.socket.data

open class RawProtocols(
    @JvmField
    var data: List<RawProtocol>
) {
    operator fun get(i: Int) = data[i]

    constructor(vararg protocols: RawProtocol): this(listOf(*protocols))
    constructor(): this(emptyList())
}