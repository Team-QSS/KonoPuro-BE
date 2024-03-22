package kr.mooner510.konopuro.domain.socket.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController {
    @GetMapping("/asdf")
    fun test(): String {
        return "asdf"
    }

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