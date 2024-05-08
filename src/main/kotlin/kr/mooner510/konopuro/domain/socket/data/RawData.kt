package kr.mooner510.konopuro.domain.socket.data

import java.util.UUID

class RawData(
    protocol: Int,
    var user: UUID,
    vararg data: String
) : RawProtocol(protocol, user, *data) {
    constructor() : this(0, UUID.randomUUID())

    operator fun get(index: Int): String {
        return data[index] as String
    }
}
