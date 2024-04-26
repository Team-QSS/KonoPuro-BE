package kr.mooner510.konopuro.domain.game.controller

import com.corundumstudio.socketio.SocketIONamespace
import kr.mooner510.konopuro.domain.game.component.GameManager
import kr.mooner510.konopuro.domain.socket.data.RawData
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/game")
class GameController(
    private val namespace: SocketIONamespace,
    private val gameManager: GameManager
) {
    @PostMapping("/match")
    fun match(@AuthenticationPrincipal user: User) {
        gameManager.matching(user)
    }

    @GetMapping("/message")
    fun message(): String {
        println("Send")
        namespace.broadcastOperations.sendEvent("chat", RawData(0, UUID.randomUUID(), ""))
        return "Success Sent"
    }
}