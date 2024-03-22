package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.card.entity.CardData
import org.springframework.data.repository.CrudRepository

interface CardDataRepository : CrudRepository<CardData, Long> {
}