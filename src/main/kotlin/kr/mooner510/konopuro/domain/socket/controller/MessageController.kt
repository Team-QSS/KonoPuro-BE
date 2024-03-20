package kr.mooner510.konopuro.domain.socket.controller

import kr.mooner510.konopuro.domain.socket.data.RawChat
import kr.mooner510.konopuro.domain.socket.exception.RoomNotFoundException
import kr.mooner510.konopuro.domain.socket.game.GameManager
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController(
    private val gameManager: GameManager
) {
//    @MessageMapping("/game")
//    fun sendMessage(
//        @Payload message: RawChat,
//        @AuthenticationPrincipal user: User
//    ) {
//        println(user.userName)
//
//        val room = gameManager.getRoomByPlayer(user.id) ?: throw RoomNotFoundException()
//        template.convertAndSend("/subscribe/${room.id}", message)
//    }
}