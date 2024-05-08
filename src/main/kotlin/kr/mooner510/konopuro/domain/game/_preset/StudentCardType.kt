package kr.mooner510.konopuro.domain.game._preset

import java.util.*

enum class StudentCardType(
    val secondTier: EnumSet<TierType>,
    val thirdPassive: EnumSet<PassiveType>,
    val forthTier: EnumSet<TierType>
) {
    SeungWoo(
        EnumSet.of(TierType.MultiMajor, TierType.SingleFocus),
        EnumSet.of(PassiveType.IdeaDay, PassiveType.Overload, PassiveType.APIRequired),
        EnumSet.of(TierType.IssueComplete, TierType.SoleDesigner, TierType.GreatDesigner)
    ),
    TaeGon(
        EnumSet.of(TierType.IssueTracker, TierType.AutonomyStudy),
        EnumSet.of(PassiveType.ProNovelist, PassiveType.TimeSaving),
        EnumSet.of(TierType.EasyFirst, TierType.CustomNovelist)
    ),
    ChulSoo(
        EnumSet.of(TierType.BeatAddFE, TierType.BeatAddBE),
        EnumSet.of(PassiveType.InfinityMusic, PassiveType.RepeatMusic),
        EnumSet.of(TierType.MusicFocus, TierType.DJ)
    ),
    JunHa(
        EnumSet.of(TierType.RegularMeeting, TierType.InfinityPassion),
        EnumSet.of(PassiveType.FastAction, PassiveType.NightCoding, PassiveType.BlazePassion),
        EnumSet.of(TierType.DoItWithTime)
    ),
    CanRobot(
        EnumSet.of(TierType.Reverse, TierType.Cooperation),
        EnumSet.of(PassiveType.Destore, PassiveType.Brocker),
        EnumSet.of(TierType.Disturbance, TierType.Reverse2)
    )

}