package kr.mooner510.konopuro.domain.socket.game

import java.util.UUID

data class GameRoom(
    val id: UUID,
    val firstUser: UUID,
    val secondUser: UUID
)
