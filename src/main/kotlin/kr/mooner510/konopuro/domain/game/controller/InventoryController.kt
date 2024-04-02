package kr.mooner510.konopuro.domain.game.controller

import kr.mooner510.konopuro.domain.game.data.card.entity.*
import kr.mooner510.konopuro.domain.game.data.card.response.PassiveResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponse
import kr.mooner510.konopuro.domain.game.data.card.response.TierResponse
import kr.mooner510.konopuro.domain.game.repository.*
import kr.mooner510.konopuro.domain.game.utils.PassiveTierUtils.toResponse
import kr.mooner510.konopuro.domain.socket.exception.CardDataNotFoundException
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/inventory")
class InventoryController(
    private val cardDataRepository: CardDataRepository,
    private val playerCardRepository: PlayerCardRepository,
    private val passiveRepository: PassiveRepository,
    private val tierRepository: TierRepository
) {
    @GetMapping
    fun getCards(
        @AuthenticationPrincipal user: User
    ) {
        val cardDataMap = hashMapOf<Long, CardData>()

        playerCardRepository.findByUserId(user.id).forEach { playerCard ->
            val cardData = cardDataMap[playerCard.cardDataId] ?: run {
                val data = cardDataRepository.findById(playerCard.cardDataId).getOrNull() ?: throw CardDataNotFoundException()
                cardDataMap[playerCard.cardDataId] = data
                data
            }
            val (passives, tiers) = playerCard.split(cardData, passiveRepository, tierRepository)
            PlayerCardResponse(
                cardData.title,
                cardData.description,
                cardData.groupSet().toList(),
                playerCard.getTier(),
                cardData.type,
                passives.toResponse(),
                tiers.toResponse()
            )
        }
    }
}