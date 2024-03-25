package kr.mooner510.konopuro.global.global.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val message: String,
    val status: HttpStatus
) {
    USER_NOT_FOUND("사용자를 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    USER_ID_ALREADY_EXISTS("해당 ID는 이미 사용 중입니다.", HttpStatus.BAD_REQUEST),

    EXPIRED_TOKEN("로그인이 만료되었습니다.", HttpStatus.FORBIDDEN),
    LOGIN_FAILED("아이디 혹은 비밀번호가 틀렸습니다.", HttpStatus.FORBIDDEN),
    INVALID_TOKEN("만료된 토큰입니다.", HttpStatus.FORBIDDEN),
    INVALID_UUID("%1은(는) 알 수 없는 UUID입니다.", HttpStatus.BAD_REQUEST),

    ROOM_NOT_FOUND("방을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    DECK_NOT_FOUND("덱을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    CLIENT_NOT_FOUND("클라이언트를 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    CARD_NOT_FOUND("카드를 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    ;

    fun parse(vararg data: Any): String {
        var msg = message
        for (i in 1 until data.size) {
            msg = msg.replaceFirst("%$i", data[i].toString())
        }
        return msg
    }
}