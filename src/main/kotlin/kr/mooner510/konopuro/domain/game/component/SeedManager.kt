package kr.mooner510.konopuro.domain.game.component

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.mooner510.konopuro.domain.game.data.deck.dto.GameDeck
import kr.mooner510.konopuro.domain.game.data.seed.entity.Seed
import kr.mooner510.konopuro.domain.game.repository.DeckCardRepository
import kr.mooner510.konopuro.domain.game.repository.DeckRepository
import kr.mooner510.konopuro.domain.game.repository.SeedRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.concurrent.thread
import kotlin.time.Duration

@Component
class SeedManager(
    private val seedRepository: SeedRepository
) {
    companion object {
        private val seedCache: ConcurrentHashMap<UUID, Int> = ConcurrentHashMap()
        private var updated: HashSet<UUID> = HashSet()
    }

    init {
        thread {
            schedule()
        }
        Runtime.getRuntime().addShutdownHook(Thread { update() })
    }

    fun schedule() = runBlocking {
        launch {
            while (true) {
                delay(60 * 1000)
                update()
            }
        }
    }

    private fun update() {
        updated.forEach {
            seedRepository.updateSeedByUserId(seedCache[it]!!, it)
        }
        updated.clear()
    }

    fun getSeed(userId: UUID): Int {
        seedCache[userId]?.let { return it }
        val seed = seedRepository.findByIdOrNull(userId)?.seed ?: 0
        seedCache[userId] = seed
        return seed
    }

    fun updateSeed(userId: UUID, func: (Int) -> Int) {
        seedCache[userId] = func(getSeed(userId))
        updated.add(userId)
    }

    fun removeSeed(userId: UUID, value: Int) {
        updateSeed(userId) { it - value }
    }

    fun addSeed(userId: UUID, value: Int) {
        updateSeed(userId) { it + value }
    }
}