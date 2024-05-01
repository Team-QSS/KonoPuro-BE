package kr.mooner510.konopuro.domain.game.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.mooner510.konopuro.domain.game.data.card.entity.*
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponse
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponses
import kr.mooner510.konopuro.domain.game.data.deck.entity.ActiveDeck
import kr.mooner510.konopuro.domain.game.data.deck.entity.Deck
import kr.mooner510.konopuro.domain.game.data.deck.entity.DeckCard
import kr.mooner510.konopuro.domain.game.data.deck.request.DeckCardRequest
import kr.mooner510.konopuro.domain.game.data.deck.request.DeckCardRequests
import kr.mooner510.konopuro.domain.game.data.deck.response.DeckResponse
import kr.mooner510.konopuro.domain.game.exception.FullDeckException
import kr.mooner510.konopuro.domain.game.repository.*
import kr.mooner510.konopuro.domain.game.utils.PassiveTierUtils.toResponse
import kr.mooner510.konopuro.domain.socket.exception.CardDataNotFoundException
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@Tag(name = "Inventory", description = "인벤 API")
@RestController
@RequestMapping("/api/inventory")
class InventoryController(
    private val cardDataRepository: CardDataRepository,
    private val playerCardRepository: PlayerCardRepository,
    private val passiveRepository: PassiveRepository,
    private val tierRepository: TierRepository,
    private val activeDeckRepository: ActiveDeckRepository,
    private val deckRepository: DeckRepository,
    private val deckCardRepository: DeckCardRepository
) {
    @Operation(summary = "소지한 카드 조회", description = "내가 가챠로 뽑아 얻은 카드를 전부 조회합니다. 한 마디로 내가 가지고 있는 카드를 전부 조회합니다.")
    @GetMapping
    fun getCards(
        @AuthenticationPrincipal user: User
    ): PlayerCardResponses {
        val cardDataMap = hashMapOf<Long, CardData>()

        return PlayerCardResponses(
            playerCardRepository.findByUserId(user.id).map { playerCard ->
                val cardData = cardDataMap[playerCard.cardDataId] ?: run {
                    val data = cardDataRepository.findById(playerCard.cardDataId).getOrNull()
                        ?: throw CardDataNotFoundException()
                    cardDataMap[playerCard.cardDataId] = data
                    data
                }
                val (passives, tiers) = playerCard.split(cardData, passiveRepository, tierRepository)
                PlayerCardResponse(
                    playerCard.id,
                    cardData.title,
                    cardData.description,
                    cardData.groupSet().toList(),
                    playerCard.getTier(),
                    cardData.type,
                    passives.toResponse(),
                    tiers.toResponse()
                )
            }
        )
    }

    @Operation(summary = "장착한 덱 조회", description = "없으면 새로운 텅 빈 defalut 덱 생성 후 반환")
    @GetMapping("/active")
    fun getActiveDeck(
        @AuthenticationPrincipal user: User
    ): DeckResponse {
        val activeDeck = activeDeckRepository.findByIdOrNull(user.id) ?: activeDeckRepository.save(
            ActiveDeck(
                user.id,
                deckRepository.save(Deck("Default", user.id)).id
            )
        )
        val deckCardList = deckCardRepository.findByDeckId(activeDeck.deckId)

        return DeckResponse(activeDeck.deckId, deckCardList.map { it.cardId })
    }

    @Operation(summary = "특정 덱에 카드 추가", description = "")
    @PostMapping("/add")
    fun addDeckCard(
        @AuthenticationPrincipal user: User,
        @RequestBody req: DeckCardRequest
    ) {
        if(deckCardRepository.countByDeckId(req.deckId) >= 25) throw FullDeckException()
        deckCardRepository.save(DeckCard(req.deckId, req.cardId))
    }

    @Operation(summary = "특정 덱에 카드 여러 개 추가", description = "")
    @PostMapping("/add-multi")
    fun addDeckCardMulti(
        @AuthenticationPrincipal user: User,
        @RequestBody req: DeckCardRequests
    ) {
        for (deckCard in req.data) {
            if(deckCardRepository.countByDeckId(deckCard.deckId) >= 25) continue
            deckCardRepository.save(DeckCard(deckCard.deckId, deckCard.cardId))
        }
    }

    @Operation(summary = "특정 덱에 카드 삭제", description = "")
    @DeleteMapping("/remove")
    fun removeDeckCard(
        @AuthenticationPrincipal user: User,
        @RequestBody req: DeckCardRequest
    ) {
        deckCardRepository.deleteByCardIdAndDeckId(req.deckId, req.cardId)
    }

    @Operation(summary = "특정 덱에 카드 여러 개 삭제", description = "")
    @PostMapping("/remove-multi")
    fun removeDeckCardMulti(
        @AuthenticationPrincipal user: User,
        @RequestBody req: DeckCardRequests
    ) {
        for (deckCard in req.data) {
            deckCardRepository.deleteByCardIdAndDeckId(deckCard.deckId, deckCard.cardId)
        }
    }
}