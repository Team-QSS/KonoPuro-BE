package kr.mooner510.konopuro.domain.socket.data.protocol

import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawProtocol

data class RoomMatchFailedEvent(
    val reason: String
) : RawProtocol(Protocol.ROOM_MATCH_FAILED)