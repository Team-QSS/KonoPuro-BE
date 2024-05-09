package kr.mooner510.konopuro.domain.game.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.mooner510.konopuro.domain.game.data.card.response.PlayerCardResponses
import kr.mooner510.konopuro.domain.game.data.deck.entity.ActiveDeck
import kr.mooner510.konopuro.domain.game.data.deck.entity.Deck
import kr.mooner510.konopuro.domain.game.data.deck.entity.DeckCard
import kr.mooner510.konopuro.domain.game.data.deck.request.ApplyDeckRequest
import kr.mooner510.konopuro.domain.game.data.deck.response.DeckResponse
import kr.mooner510.konopuro.domain.game.repository.*
import kr.mooner510.konopuro.global.security.data.entity.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Inventory", description = "인벤 API")
@RestController
@RequestMapping("/api/inventory")
class InventoryController(
    private val playerCardRepository: PlayerCardRepository,
    private val activeDeckRepository: ActiveDeckRepository,
    private val deckRepository: DeckRepository,
    private val deckCardRepository: DeckCardRepository
) {
    @Operation(summary = "소지한 카드 조회", description = "내가 가챠로 뽑아 얻은 카드를 전부 조회합니다. 한 마디로 내가 가지고 있는 카드를 전부 조회합니다.")
    @GetMapping
    fun getCards(
        @AuthenticationPrincipal user: User
    ): PlayerCardResponses {
        return PlayerCardResponses(playerCardRepository.findByUserId(user.id).map { it.toResponse() })
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

    @Operation(summary = "수정한 덱 정보 적용", description = "")
    @PostMapping("/apply")
    fun applyDeck(
        @AuthenticationPrincipal user: User,
        @RequestBody req: ApplyDeckRequest
    ) {
        deckCardRepository.saveAll(req.addition.map { DeckCard(req.activeDeckId, it) })
        deckCardRepository.deleteAll(req.deletion.map { DeckCard(req.activeDeckId, it) })
    }
}