package kr.mooner510.konopuro.domain.socket.data

import java.util.UUID

data class RawData(
    var protocol: Int,
    var user: UUID,
    var data: List<Any>
) {
    constructor() : this(0, UUID.randomUUID(), emptyList())
}
