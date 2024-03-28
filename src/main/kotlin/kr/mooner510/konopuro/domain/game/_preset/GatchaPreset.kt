package kr.mooner510.konopuro.domain.game._preset


import kr.mooner510.konopuro.domain.game.data.gatcha.entity.Gatcha
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.game.repository.GatchaRepository
import org.springframework.stereotype.Component

@Component
class GatchaPreset(
    private val gatchaRepository: GatchaRepository

) {
    init {
        if (!gatchaRepository.existsByTitle("Test Gatcha")) {
            gatchaRepository.save(
                Gatcha(
                    "Test Gatcha",
                    MajorType.Design
                )
            )
        }
    }
}