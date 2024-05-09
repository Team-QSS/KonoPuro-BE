package kr.mooner510.konopuro.domain.game._preset

import kr.mooner510.konopuro.domain.game.controller.CardController
import kr.mooner510.konopuro.domain.game.data.card.request.CreateCardRequest
import kr.mooner510.konopuro.domain.game.data.card.request.PassiveRequest
import kr.mooner510.konopuro.domain.game.data.card.request.TierRequest
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.game.exception.CardAlreadyExistsException
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull
import kotlin.properties.Delegates

@Component
class CardPreset(
    private val cardController: CardController

) {
    private var designer by Delegates.notNull<Long>()
    private var frontend by Delegates.notNull<Long>()
    private var backend by Delegates.notNull<Long>()
    private var iOS by Delegates.notNull<Long>()
    private var android by Delegates.notNull<Long>()

    init {
        listOf(
            CreateCardRequest(
                "최승우",
                "",
                listOf(MajorType.FrontEnd, MajorType.Design),
                CardType.Student,
                designer,
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
            ),
            CreateCardRequest(
                "임태곤",
                "",
                listOf(MajorType.FrontEnd),
                CardType.Student,
                frontend,
                listOf(
                    PassiveRequest(
                        "소설가",
                        "수면 시간의 1시간은 소설을 본다. 소설을 본 시간 만큼 다음날 능력 사용에 필요한 시간이 감소한다. 소설을 본 시간은 수면 시간으로 인정되지 않는다."
                    )
                ),
                listOf(
                    TierRequest(
                        "이슈 트래커",
                        "프론트엔드 진행도가 10pt 증가한다. 프론트엔드 이슈가 존재할 경우, 이슈가 요구하는 진행도 만큼 증가 시킨다. 최대 20pt까지 증가할 수 있다.",
                        6
                    ),
                    TierRequest(
                        "자율 학습",
                        "프론트엔드 진행도가 16pt 증가하고 열기를 획득한다. 열기 1스택 마다 자신이 프론트엔드 진행도를 올렸을 때 이슈가 발생할 확률이 8%pt씩 감소한다. 열기는 최대 5스택 까지 존재할 수 있으며, 한 열기는 5일 간 유지된다.",
                        9
                    ),
                ),
                listOf(
                    PassiveRequest(
                        "프로 소설가",
                        "소설가 능력의 소설을 보는 시간이 다음과 같이 변경된다: 수면 시간이 7시간 이하일 경우 1시간 소설을 보고, 7시간 초과일 경우 최대 2시간 소설을 본다."
                    ),
                    PassiveRequest(
                        "시간 절약",
                        "같은 프론트엔드 분야 학생이 있을 경우 모든 학생이 피로도로 인해 능력 요구 시간 증가 효과를 1.5pt마다 1시간으로 변경한다."
                    ),
                ),
                listOf(
                    TierRequest(
                        "쉬운 일은 먼저",
                        "프론트엔드 진행도가 25pt 증가한다.",
                        11
                    ),
                    TierRequest(
                        "맞춤 소설가",
                        "프론트엔드 진행도가 9pt 증가한다. 지금까지 소설을 읽은 시간의 20%만큼 추가로 진행도가 증가한다.",
                        6
                    ),
                )
            ),
            CreateCardRequest(
                "철수",
                "",
                listOf(MajorType.Backend),
                CardType.Student,
                cardController.createTier(
                    TierRequest(
                        "비트 추가",
                        "프론트엔드 진행도를 2pt 증가 시키고, 백엔드 진행도를 2pt 증가 시킨다. [음악]을 추가로 하나 더 재생한다.",
                        0
                    )
                ).id,
                listOf(
                    PassiveRequest(
                        "뮤직 플레이",
                        "하루가 시작할 때마다 [음악]을 재생한다."
                    ),
                    PassiveRequest(
                        "음악",
                        "재생되는 동안 모든 팀원에게 모든 분야의 효율을 20pt 증가 시킨다. 하루가 지날 경우 재생되고 있는 음악은 즉시 종료된다. 중첩이 가능하다."
                    ),
                ),
                listOf(
                    TierRequest(
                        "비트 추가: 프론트엔드",
                        "프론트엔드 진행도를 8pt 증가 시키고 [음악]을 추가로 하나 더 재생한다.",
                        6
                    ),
                    TierRequest(
                        "비트 추가: 백엔드 ",
                        "백엔드 진행도를 8pt 증가 시키고 [음악]을 추가로 하나 더 재생한다.",
                        6
                    ),
                ),
                listOf(
                    PassiveRequest(
                        "지치지 않는 음악",
                        "[음악]이 재생되는 동안 쌓이는 피로도는 10%p 감소한다."
                    ),
                    PassiveRequest(
                        "반복 재생",
                        "[음악]이 하루가 지나 재생이 중단되었을 때 8% 확률로 중단되지 않고 다시 재생한다. 각 확률은 [음악]마다 따로 계산한다."
                    ),
                ),
                listOf(
                    TierRequest(
                        "집중!",
                        "프론트엔드 진행도 보다 백엔드 진행도가 낮을 경우, 백엔드 진행도를 4pt 증가 시킨다.\n" +
                                "아닐 경우 프론트엔드 진행도를 1pt 증가시킨다. 해당 능력을 사용할 만큼 시간이 남았을 경우, 재생되고 있는 음악의 수에 따라 해당 능력으로 증가하는 진행도는 1pt씩 증가한다.",
                        3
                    ),
                    TierRequest(
                        "디제이",
                        "재생되고 있는 모든 [음악]을 중단하고 저장한다. [뮤직 플레이] 능력이 사용될 때 저장된 음악의 수 만큼 추가로 재생한다.",
                        3
                    ),
                )
            ),
            CreateCardRequest(
                "박준하",
                "",
                listOf(MajorType.iOS),
                CardType.Student,
                iOS,
                listOf(
                    PassiveRequest(
                        "연속 개발",
                        "팀이 지금까지 개발한 진행도의 5% 만큼 모든 개발 효율이 상승한다."
                    )
                ),
                listOf(
                    TierRequest(
                        "정기 회의",
                        "오늘 하루 동안 모든 팀원의 모든 개발 효율이 50% 증가한다.",
                        3
                    ),
                    TierRequest(
                        "끝없는 열정",
                        "iOS 진행도를 20pt 증가시킨다. 이 능력으로 iOS 개발 이슈가 생성될 확률은 기존보다 25% 높다.",
                        7
                    ),
                ),
                listOf(
                    PassiveRequest(
                        "빠른 조치",
                        "팀에 이슈가 발생하였을 경우, 해당 이슈의 요구 진행도를 15% 감소시킨다."
                    ),
                    PassiveRequest(
                        "밤샘 코딩",
                        "오늘 수면을 하지 않았을 경우 오늘 증가 시킨 모든 분야의 진행도가 2배 상승한다."
                    ),
                    PassiveRequest(
                        "타오르는 열정",
                        "피로 및 피곤 상태 효과가 모든 팀원에게 다음과 같이 적용된다: 해당 상태에 빠지지 않고 해당 상태에 빠지게 된 날을 1일로 간주하여 이후 4일 간 해당 상태에 면역이 주어진다. 면역이 끝난 이후 3일 간 해당 상태 조건에 만족하게 되면, 끈기와 열정 효과는 해당 프로젝트에서 더 이상 효과가 발동되지 않는다."
                    ),
                ),
                listOf(
                    TierRequest(
                        "시간적 해결",
                        "팀에 존재하는 이슈 중 분야에 상관 없이 가장 요구 진행도가 많은 최대 3개의 이슈를 해결 완료 상태로 만든다.",
                        17
                    ),
                )
            ),
            CreateCardRequest(
                "깡통 로봇",
                "",
                listOf(MajorType.Android),
                CardType.Student,
                android,
                listOf(
                    PassiveRequest(
                        "마스터링",
                        "상대의 iOS, 안드로이드 분야 개발 효율이 25pt 감소한다."
                    ),
                    PassiveRequest(
                        "이슈 크랙커",
                        "상대가 이슈가 발생할 확률이 10% 상승한다"
                    ),
                ),
                listOf(
                    TierRequest(
                        "역행",
                        "안드로이드 진행도를 5pt 증가 시키고, 상대의 안드로이드 진행도를 5pt 감소 시킨다.",
                        5
                    ),
                    TierRequest(
                        "협업",
                        "자신을 제외한 안드로이드 분야인 다른 학생이 있을 경우, 두 학생 모두 안드로이드 개발 효율이 이틀 간 50pt 증가한다.",
                        3
                    ),
                ),
                listOf(
                    PassiveRequest(
                        "디스토어",
                        "상대가 안드로이드 혹은 iOS 진행도를 증가시킬 경우, 자신의 팀에도 해당 진행도를 2pt 증가시킨다"
                    ),
                    PassiveRequest(
                        "브로커",
                        "짝수 날이 될 때마다 상대는 1시간을 소모한 상태로 진행한다."
                    ),
                ),
                listOf(
                    TierRequest(
                        "방해",
                        "상대의 1명의 학생을 선택하여 오늘 능력을 사용할 수 없도록 만든다.",
                        5
                    ),
                    TierRequest(
                        "역행 II",
                        "상대의 안드로이드 진행도를 13pt 감소시킨다.",
                        6
                    ),
                )
            ),
//            CreateCardRequest(
//                "",
//                "",
//                listOf(MajorType.FrontEnd),
//                CardType.Student,
//                startTiers[1],
//                listOf(
//                    PassiveRequest(
//                        "",
//                        ""
//                    )
//                ),
//                listOf(
//                    TierRequest(
//                        "",
//                        "",
//                        0
//                    ),
//                    TierRequest(
//                        "",
//                        "",
//                        0
//                    ),
//                ),
//                listOf(
//                    PassiveRequest(
//                        "",
//                        ""
//                    ),
//                    PassiveRequest(
//                        "",
//                        ""
//                    ),
//                ),
//                listOf(
//                    TierRequest(
//                        "",
//                        "",
//                        0
//                    ),
//                    TierRequest(
//                        "",
//                        "",
//                        0
//                    ),
//                )
//            ),
        ).map {
            try {
                cardController.createCardData(it)
            } catch (_: CardAlreadyExistsException) {
            }
        }
    }
}