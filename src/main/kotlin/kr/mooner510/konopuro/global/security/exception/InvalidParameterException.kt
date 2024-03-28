package kr.mooner510.konopuro.global.security.exception

import kr.mooner510.konopuro.global.global.error.ErrorCode
import kr.mooner510.konopuro.global.global.error.data.GlobalError

class InvalidParameterException : GlobalError(ErrorCode.INVALID_PARAMETER)