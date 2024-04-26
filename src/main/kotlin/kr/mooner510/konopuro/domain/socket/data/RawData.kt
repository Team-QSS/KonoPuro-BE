package kr.mooner510.konopuro.domain.socket.data

import java.util.UUID

class RawData(
    protocol: Int,
    var user: UUID,
    vararg data: Any
): RawProtocol(protocol, user, *data) {
    constructor() : this(0, UUID.randomUUID())
}
