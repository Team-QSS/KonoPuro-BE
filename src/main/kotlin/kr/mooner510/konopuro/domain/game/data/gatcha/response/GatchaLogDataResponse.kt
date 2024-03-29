package kr.mooner510.konopuro.domain.game.data.gatcha.response

data class GatchaLogDataResponse(
    val total: Long,
    val filtered: Int,
    val stack3: Int,
    val stack4: Int,
    val full3: Boolean,
    val full4: Boolean,
    val data: List<GatchaLogResponse>
)