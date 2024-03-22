package kr.mooner510.konopuro.domain.socket.exception

import kr.mooner510.konopuro.global.global.error.ErrorCode
import kr.mooner510.konopuro.global.global.error.data.GlobalError

class UserClientNotFoundException : GlobalError(ErrorCode.ROOM_NOT_FOUND)