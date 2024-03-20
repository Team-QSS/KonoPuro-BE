package kr.mooner510.konopuro.global.global.exception

import kr.mooner510.konopuro.global.global.error.ErrorCode
import kr.mooner510.konopuro.global.global.error.data.GlobalError

class InvalidUUIDException(target: String) : GlobalError(ErrorCode.INVALID_UUID, target)