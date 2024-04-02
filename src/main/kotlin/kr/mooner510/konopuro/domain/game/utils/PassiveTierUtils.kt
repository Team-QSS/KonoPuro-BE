package kr.mooner510.konopuro.domain.game.utils

import kr.mooner510.konopuro.domain.game.data.card.entity.Passive
import kr.mooner510.konopuro.domain.game.data.card.entity.Tier
import kr.mooner510.konopuro.domain.game.data.card.response.PassiveResponse
import kr.mooner510.konopuro.domain.game.data.card.response.TierResponse

object PassiveTierUtils {
    fun Iterable<Passive>.toResponse() = this.map { PassiveResponse(it.id, it.title, it.description) }
    fun Collection<Tier>.toResponse() = this.map { TierResponse(it.id, it.title, it.description, it.time) }
}