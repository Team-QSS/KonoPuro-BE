package kr.mooner510.konopuro.global.global.error.data

import kr.mooner510.konopuro.global.global.error.ErrorCode

open class GlobalError(
    val errorCode: ErrorCode,
    vararg args: Any
) : RuntimeException(errorCode.parse(args))