package kr.mooner510.konopuro.domain.game.controller

import com.corundumstudio.socketio.SocketIONamespace
import kr.mooner510.konopuro.domain.game.component.GameManager
import kr.mooner510.konopuro.domain.game.data.card.entity.CardData
import kr.mooner510.konopuro.domain.game.data.card.entity.Passive
import kr.mooner510.konopuro.domain.game.data.card.entity.PlayerCard
import kr.mooner510.konopuro.domain.game.data.card.entity.Tier
import kr.mooner510.konopuro.domain.game.data.card.request.CreateCardRequest
import kr.mooner510.konopuro.domain.game.data.card.response.CardDataResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PassiveResponse
import kr.mooner510.konopuro.domain.game.data.card.response.TierResponse
import kr.mooner510.konopuro.domain.game.repository.CardDataRepository
import kr.mooner510.konopuro.domain.game.repository.PassiveRepository
import kr.mooner510.konopuro.domain.game.repository.PlayerCardRepository
import kr.mooner510.konopuro.domain.game.repository.TierRepository
import kr.mooner510.konopuro.domain.socket.data.RawData
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import java.util.*
import kotlin.math.pow

@RestController
@RequestMapping("/api/card")
class CardController(
    private val cardDataRepository: CardDataRepository,
    private val tierRepository: TierRepository,
    private val passiveRepository: PassiveRepository
) {
    @GetMapping
    fun getCard(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) id: Long?
    ) {
        val cardData: Optional<CardData> = id?.let { cardDataRepository.findById(id) } ?: name?.let { cardDataRepository.findByTitleStartsWith(it) }

        val tiers = tierRepository.findAllById(listOf())
    }

    @PostMapping
    fun createCard(
        @RequestBody req: CreateCardRequest
    ): CardDataResponse {
        val tiers = req.tiers.map {
            tierRepository.save(
                Tier(
                    it.title,
                    it.description,
                    it.time
                )
            )
        }

        val passives = req.passives.map {
            passiveRepository.save(
                Passive(
                    it.title,
                    it.description
                )
            )
        }

        val cardData = cardDataRepository.save(
            CardData(
                req.title,
                req.description,
                req.cardGroups.sumOf { 2.0.pow(it.ordinal) }.toLong(),
                req.type,
                tiers[0].id,
                passives[0].id,
                if (passives.size > 1) passives[1].id else null,
                if (passives.size > 2) passives[2].id else null
            )
        )

        return CardDataResponse(
            cardData.title,
            cardData.description,
            cardData.groupSet().toList(),
            cardData.type,
            passives.map { PassiveResponse(it.id, it.title, it.description) },
            tiers.map { TierResponse(it.id, it.title, it.description, it.time) }
        )
    }
}