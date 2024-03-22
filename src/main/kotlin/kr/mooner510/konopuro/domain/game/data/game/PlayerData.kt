package kr.mooner510.konopuro.domain.game.data.game

import kr.mooner510.konopuro.domain.game.data.card.dto.GameCard
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import java.util.LinkedList
import java.util.UUID

data class PlayerData(
    val id: UUID,
    val client: UUID,
    val students: MutableList<GameCard>,
    val deckList: LinkedList<GameCard>,
    val heldCards: MutableList<GameCard>,
    var time: Int,
    val fieldCards: MutableList<GameCard>,
    val project: MutableMap<MajorType, Int>,
    val issue: MutableMap<MajorType, MutableList<Int>>,
    val goal: Map<MajorType, Int>,
)
