package kr.mooner510.konopuro.domain.game.component

import kr.mooner510.konopuro.domain.game.controller.CardController
import kr.mooner510.konopuro.domain.game.data.card.entity.Passive
import kr.mooner510.konopuro.domain.game.data.card.entity.Tier
import kr.mooner510.konopuro.domain.game.data.card.request.CreateCardRequest
import kr.mooner510.konopuro.domain.game.data.card.request.PassiveRequest
import kr.mooner510.konopuro.domain.game.data.card.request.TierRequest
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.game.exception.CardAlreadyExistsException
import kr.mooner510.konopuro.domain.game.repository.CardDataRepository
import kr.mooner510.konopuro.domain.game.repository.PassiveRepository
import kr.mooner510.konopuro.domain.game.repository.TierRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class CardPreset(
    private val cardDataRepository: CardDataRepository,
    private val tierRepository: TierRepository,
    private val passiveRepository: PassiveRepository,
    private val cardController: CardController

) {
    init {
        val startTiers = listOf(
            (tierRepository.findByTitle("디자이너").getOrNull() ?: tierRepository.save(Tier("디자이너", "디자인 진행도를 6pt 증가시킨다.", 3))).id,
            (tierRepository.findByTitle("프론트엔드").getOrNull() ?: tierRepository.save(Tier("프론트엔드", "프론트엔드 진행도를 6pt 증가 시킨다.", 3))).id,
            (tierRepository.findByTitle("iOS").getOrNull() ?: tierRepository.save(Tier("iOS", "iOS 진행도를 8pt 증가 시킨다.", 4))).id,
            (tierRepository.findByTitle("안드로이드").getOrNull() ?: tierRepository.save(Tier("안드로이드", "안드로이드 진행도를 8pt 증가 시킨다.", 4))).id,
        )

        listOf(
            CreateCardRequest(
                "최승우",
                "",
                listOf(MajorType.FrontEnd, MajorType.Design),
                CardType.Student,
                startTiers[1],
                listOf(
                    PassiveRequest(
                        "병행 프로세스",
                        "어제 디자인 및 프론트를 진행도를 올리지 않았다면, 오늘 능력으로 디자인 및 프론트 진행도를 올릴 때마다 2pt 추가로 증가한다."
                    )
                ),
                listOf(
                    TierRequest(
                        "멀티 전공",
                        "디자인이 완료되지 않았을 경우 디자인의 진행도를 11pt 증가 시킨다.\n" +
                                "디자인이 완료되었다면 프론트엔드의 진행도를 7pt 증가 시킨다.",
                        5
                    ),
                    TierRequest(
                        "하나만 집중하기",
                        "프론트엔드의 진행도를 4pt 증가 시킨다. 만약 오늘 디자인 진행도를 올리지 않았을 경우, 추가로 1pt 증가 시킨다.",
                        2
                    ),
                ),
                listOf(
                    PassiveRequest(
                        "아이디어가 샘솟는 날",
                        "이틀마다 이틀 동안 디자인 혹은 프론트엔드 효율이 무작위로 25% 증가한다."
                    ),
                    PassiveRequest(
                        "오버로드",
                        "자신 외에 프론트엔드 분야인 다른 학생이 존재한다면, 모든 학생의 프론트엔드 효율을 30% 증가 시킨다."
                    ),
                    PassiveRequest(
                        "API 요구",
                        "백엔드 진행도가 프론트엔드 진행도의 80%를 미치지 못할 경우 모든 학생의 백엔드 효율이 20% 증가한다."
                    ),
                ),
                listOf(
                    TierRequest(
                        "이슈 해결",
                        "프론트엔드 진행도를 4pt 증가 시킨다. 프론트엔드에 이슈가 있을 경우 이슈 하나 마다 추가로 3pt 증가 시킨다.",
                        3
                    ),
                    TierRequest(
                        "유일한 디자이너",
                        "디자인 진행도를 10pt 증가 시킨다. 추가로 상대의 디자인 진행도를 하루에 1pt씩 3일간 감소 시킨다.",
                        6
                    ),
                    TierRequest(
                        "뛰어난 디자이너",
                        "3일 간 상대의 디자인 효율을 25pt 감소 시킨다. 중첩이 가능하다.",
                        6
                    ),
                )
            )
        ).map {
            try {
                cardController.createCardData(it)
            } catch (_: CardAlreadyExistsException) {
            }
        }
    }

    fun passive(passive: Passive): Long {
        return passiveRepository.save(passive).id
    }
}