package kr.mooner510.konopuro.domain.socket.data.protocol

import kr.mooner510.konopuro.domain.socket.data.Protocol
import kr.mooner510.konopuro.domain.socket.data.RawProtocol

data class GameEndEvent(
    val reason: String
) : RawProtocol(Protocol.GAME_END)