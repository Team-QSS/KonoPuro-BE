package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.deck.entity.Deck
import org.springframework.data.repository.CrudRepository
import java.util.*

interface DeckRepository : CrudRepository<Deck, UUID> {


    fun findByUserId(userId: UUID): List<Deck>
}