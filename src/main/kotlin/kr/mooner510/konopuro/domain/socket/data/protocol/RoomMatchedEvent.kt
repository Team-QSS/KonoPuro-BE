package kr.mooner510.konopuro.domain.socket.data.protocol

import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawProtocol
import java.util.*

data class RoomMatchedEvent(
    val roomId: UUID
) : RawProtocol(Protocol.ROOM_MATCHED)