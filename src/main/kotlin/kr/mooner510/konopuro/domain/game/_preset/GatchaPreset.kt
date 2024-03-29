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
        listOf(
            Gatcha(
                "Frontend Gatcha",
                MajorType.FrontEnd,
                LocalDateTime.now().plusDays(-1).withHour(12).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(13).withHour(12).withMinute(0).withSecond(0).withNano(0)
            ),
            Gatcha(
                "Backend Gatcha",
                MajorType.Backend,
                LocalDateTime.now().plusDays(-1).withHour(12).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(13).withHour(12).withMinute(0).withSecond(0).withNano(0)
            ),
            Gatcha(
                "iOS Gatcha",
                MajorType.IOS,
                LocalDateTime.now().plusDays(-1).withHour(12).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(13).withHour(12).withMinute(0).withSecond(0).withNano(0)
            ),
            Gatcha(
                "Android Gatcha",
                MajorType.Android,
                LocalDateTime.now().plusDays(-1).withHour(12).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(13).withHour(12).withMinute(0).withSecond(0).withNano(0)
            ),
            Gatcha(
                "Design Gatcha",
                MajorType.Design,
                LocalDateTime.now().plusDays(-1).withHour(12).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(13).withHour(12).withMinute(0).withSecond(0).withNano(0)
            ),
        ).forEach {
            if (!gatchaRepository.existsByTitle(it.title)) {
                gatchaRepository.save(it)
            }
        }
    }
}