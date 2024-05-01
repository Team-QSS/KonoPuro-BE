package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.deck.entity.DeckCard
import org.springframework.data.repository.CrudRepository
import java.util.*

interface DeckCardRepository : CrudRepository<DeckCard, UUID> {


    fun findByDeckId(deckId: UUID): List<DeckCard>

}