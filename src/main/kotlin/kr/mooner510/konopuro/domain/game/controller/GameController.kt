package kr.mooner510.konopuro.domain.game.controller

import com.corundumstudio.socketio.SocketIONamespace
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.mooner510.konopuro.domain.game.component.GameManager
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Game", description = "게임 로직 API")
@RestController
@RequestMapping("/api/game")
class GameController(
    private val namespace: SocketIONamespace,
    private val gameManager: GameManager
) {
    @Operation(summary = "매칭 시작", description = "나를 매칭 큐에 넣어서 사람을 찾아~ 취소 못함 ㅋ")
    @PostMapping("/match")
    fun match(
        @AuthenticationPrincipal user: User
    ) {
        gameManager.matching(user)
    }
}
