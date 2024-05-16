package kr.mooner510.konopuro.domain.game._preset

enum class TierType(val time: Int) {
    // 기본
    Designer(3),
    Frontend(3),
    Backend(3),
    iOS(4),
    Android(4),

    // 최승우
    MultiMajor(5),
    SingleFocus(2),
    IssueComplete(4),
    SoleDesigner(6),
    GreatDesigner(6),

    // 임태곤
    IssueTracker(6),
    AutonomyStudy(9),
    EasyFirst(11),
    CustomNovelist(6),

    // 철수
    AddBeat(4),
    BeatAddFE(6),
    BeatAddBE(6),
    MusicFocus(3),
    DJ(1),

    // 준하
    RegularMeeting(3),
    InfinityPassion(7),
    DoItWithTime(24),

    // 깡통 로봇
    Reverse(5),
    Cooperation(3),
    Disturbance(5),
    Reverse2(6)

}