package kr.mooner510.konopuro.domain.game.repository;

import kr.mooner510.konopuro.domain.game.data.card.entity.CardInventory
import org.springframework.data.repository.CrudRepository

interface CardInventoryRepository : CrudRepository<CardInventory, Long> {


    fun findByUserId(userId: Long): List<CardInventory>
}