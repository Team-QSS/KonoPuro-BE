package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.deck.entity.ActiveDeck
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ActiveDeckRepository : CrudRepository<ActiveDeck, UUID> {
}