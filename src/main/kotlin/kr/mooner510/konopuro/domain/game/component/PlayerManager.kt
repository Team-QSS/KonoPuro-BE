package kr.mooner510.konopuro.domain.game.component

import kr.mooner510.konopuro.domain.game.data.deck.dto.GameDeck
import kr.mooner510.konopuro.domain.game.repository.DeckCardRepository
import kr.mooner510.konopuro.domain.game.repository.DeckRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PlayerManager(
    private val deckCardRepository: DeckCardRepository,
    private val deckRepository: DeckRepository
) {
    fun getDecks(userId: UUID): List<GameDeck> {
        return deckRepository.findByUserId(userId).map {
            val deckCards = deckCardRepository.findByDeckId(userId)
            GameDeck(userId, it.title, deckCards)
        }
    }
}