package kr.mooner510.konopuro.domain.game.data.gatcha.response

data class GatchaLogDataResponse(
    val total: Int,
    val stack3: Int,
    val stack4: Int,
    val data: List<GatchaLogResponse>
)