package kr.mooner510.konopuro.domain.game._preset

import kr.mooner510.konopuro.domain.game.data.card.response.StudentDataResponse
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import java.util.*

enum class StudentCardType(
    val major: EnumSet<MajorType>,
    val passive: EnumSet<PassiveType>,
    val tier: TierType,
    val secondTier: EnumSet<TierType>,
    val thirdPassive: EnumSet<PassiveType>,
    val forthTier: EnumSet<TierType>
) {
    SeungWoo(
        EnumSet.of(MajorType.FrontEnd, MajorType.Design),
        EnumSet.of(PassiveType.ParallelProcess),
        TierType.Designer,
        EnumSet.of(TierType.MultiMajor, TierType.SingleFocus),
        EnumSet.of(PassiveType.IdeaDay, PassiveType.Overload, PassiveType.APIRequired),
        EnumSet.of(TierType.IssueComplete, TierType.SoleDesigner, TierType.GreatDesigner)
    ),
    TaeGon(
        EnumSet.of(MajorType.FrontEnd),
        EnumSet.of(PassiveType.Novelist),
        TierType.Frontend,
        EnumSet.of(TierType.IssueTracker, TierType.AutonomyStudy),
        EnumSet.of(PassiveType.ProNovelist, PassiveType.TimeSaving),
        EnumSet.of(TierType.EasyFirst, TierType.CustomNovelist)
    ),
    ChulSoo(
        EnumSet.of(MajorType.Backend, MajorType.FrontEnd),
        EnumSet.of(PassiveType.MusicPlay, PassiveType.Music),
        TierType.AddBeat,
        EnumSet.of(TierType.BeatAddFE, TierType.BeatAddBE),
        EnumSet.of(PassiveType.InfinityMusic, PassiveType.RepeatMusic),
        EnumSet.of(TierType.MusicFocus, TierType.DJ)
    ),
    JunHa(
        EnumSet.of(MajorType.iOS),
        EnumSet.of(PassiveType.MultiDevelop),
        TierType.iOS,
        EnumSet.of(TierType.RegularMeeting, TierType.InfinityPassion),
        EnumSet.of(PassiveType.FastAction, PassiveType.NightCoding, PassiveType.BlazePassion),
        EnumSet.of(TierType.DoItWithTime)
    ),
    CanRobot(
        EnumSet.of(MajorType.Android),
        EnumSet.of(PassiveType.Mastering, PassiveType.IssueCracker),
        TierType.Android,
        EnumSet.of(TierType.Reverse, TierType.Cooperation),
        EnumSet.of(PassiveType.Destore, PassiveType.Brocker),
        EnumSet.of(TierType.Disturbance, TierType.Reverse2)
    );

    fun toResponse(): StudentDataResponse {
        return StudentDataResponse(
            toString(),
            CardType.Student,
            major.sortedBy { it.ordinal },
            passive.sortedBy { it.ordinal },
            tier,
            secondTier.sortedBy { it.ordinal },
            thirdPassive.sortedBy { it.ordinal },
            forthTier.sortedBy { it.ordinal }
        )
    }

}