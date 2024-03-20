package kr.mooner510.konopuro.domain.socket.controller

import com.corundumstudio.socketio.SocketIOServer
import kr.mooner510.konopuro.domain.socket.data.RawChat
import kr.mooner510.konopuro.domain.socket.exception.RoomNotFoundException
import kr.mooner510.konopuro.domain.socket.game.GameManager
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/game")
class GameController(
    private val server: SocketIOServer,
    private val gameManager: GameManager
) {
    @PostMapping("/match")
    fun match(@AuthenticationPrincipal user: User) {
        gameManager.matching(user)
    }

//    @MessageMapping
//    fun sendMessage(
//        @Payload message: RawChat,
//        @AuthenticationPrincipal user: User
//    ) {
//        println(user.userName)
//
//        val room = gameManager.getRoomByPlayer(user.id) ?: throw RoomNotFoundException()
//        user.client?.let {
//            server.getClient(it).sendEvent("", message)
//        }
//    }
}