package kr.mooner510.konopuro.domain.game.exception

import kr.mooner510.konopuro.global.global.error.ErrorCode
import kr.mooner510.konopuro.global.global.error.data.GlobalError

class GatchaNotFoundException : GlobalError(ErrorCode.GATCHA_NOT_FOUND)