package kr.mooner510.konopuro.domain.game._preset

import kr.mooner510.konopuro.domain.game.controller.DataController

enum class TierType {
    // 기본
    Designer,
    Frontend,
    Backend,
    iOS,
    Android,

    // 최승우
    MultiMajor,
    SingleFocus,
    IssueComplete,
    SoleDesigner,
    GreatDesigner,

    // 임태곤
    IssueTracker,
    AutonomyStudy,
    EasyFirst,
    CustomNovelist,

    // 철수
    AddBeat,
    BeatAddFE,
    BeatAddBE,
    MusicFocus,
    DJ,

    // 준하
    RegularMeeting,
    InfinityPassion,
    DoItWithTime,

    // 깡통 로봇
    Reverse,
    Cooperation,
    Disturbance,
    Reverse2;

    companion object {
        private val timeMap = hashMapOf<TierType, Int>()

        fun setTime(type: TierType, time: Int) {
            timeMap[type] = time
        }

        fun getTime(type: TierType): Int {
            DataController.tierUpdater()
            return timeMap.getOrDefault(type, 0)
        }
    }

    val time: Int
        get() {
            return getTime(this)
        }
}
