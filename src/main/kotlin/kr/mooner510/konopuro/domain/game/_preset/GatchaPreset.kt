package kr.mooner510.konopuro.domain.game._preset


import kr.mooner510.konopuro.domain.game.data.gatcha.entity.Gatcha
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.game.repository.GatchaRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class GatchaPreset(
    private val gatchaRepository: GatchaRepository

) {
    init {
        if (!gatchaRepository.existsByTitle("Test Gatcha")) {
            gatchaRepository.save(
                Gatcha(
                    "Test Gatcha",
                    MajorType.Design,
                    LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0),
                    LocalDateTime.now().plusDays(14).withHour(12).withMinute(0).withSecond(0).withNano(0)
                )
            )
        }
    }
}