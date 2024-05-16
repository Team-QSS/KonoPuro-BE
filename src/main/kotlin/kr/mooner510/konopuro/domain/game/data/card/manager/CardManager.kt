package kr.mooner510.konopuro.domain.game.data.card.manager

import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game._preset.TierType.*
import kr.mooner510.konopuro.domain.game.data.card.types.StudentState
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.socket.data.game.PlayerData
import kr.mooner510.konopuro.domain.socket.data.type.DataKey

object CardManager {
    fun PlayerData.PlayerDataModifier.useTier(tierType: TierType) = execute {
        activeStudent = this.students.find { it.tiers.contains(tierType) }!!
        if (!removeTime(tierType.time)) return@execute false
        when (tierType) {
            Designer -> addProject(MajorType.Design, 6)
            Frontend -> addProject(MajorType.FrontEnd, 6)
            Backend -> addProject(MajorType.Backend, 6)
            iOS -> addProject(MajorType.iOS, 8)
            Android -> addProject(MajorType.Android, 8)
            MultiMajor ->
                if (isDone(MajorType.Design)) {
                    addProject(MajorType.Design, 11)
                } else {
                    addProject(MajorType.FrontEnd, 7)
                }

            SingleFocus ->
                if (getInt(DataKey.DesignProject, 0) > 0) {
                    addProject(MajorType.FrontEnd, 4)
                } else {
                    addProject(MajorType.FrontEnd, 5)
                }

            IssueComplete -> addProject(MajorType.FrontEnd, 4 + (issue[MajorType.FrontEnd]?.size ?: 0) * 3)
            SoleDesigner -> {
                addProject(MajorType.Design, 8)
                addFieldCard(DefaultCardType.OnlyPower, 3, false)
            }

            GreatDesigner -> {
                addProject(MajorType.Design, 4)
                addFieldCard(DefaultCardType.UltimatePower, 3, true)
            }

            IssueTracker -> addProject(MajorType.FrontEnd, 9 + (issue[MajorType.FrontEnd]?.sum()?.coerceAtMost(20) ?: 0))
            AutonomyStudy -> {
                addProject(MajorType.FrontEnd, 12)
                val filtered = students.filter { !it.hasEndDate(StudentState.Passion) }
                if (filtered.isEmpty()) {
                    val minTime = students.minOf { it.getEndDate(StudentState.Passion, 4) }
                    modifyStudent(students.filter { it.getEndDate(StudentState.Passion) == minTime }.random()) {
                        it.setEndDate(StudentState.Passion, 4)
                    }
                } else {
                    modifyStudent(filtered.random()) {
                        it.setEndDate(StudentState.Passion, 4)
                    }
                }
            }

            EasyFirst -> {
                addProject(MajorType.FrontEnd, 22)
            }

            CustomNovelist -> {
                addProject(MajorType.FrontEnd, getInt(DataKey.NovelTime, 0))
            }

            AddBeat -> TODO()
            BeatAddFE -> TODO()
            BeatAddBE -> TODO()
            MusicFocus -> TODO()
            DJ -> TODO()
            RegularMeeting -> TODO()
            InfinityPassion -> TODO()
            DoItWithTime -> TODO()
            Reverse -> TODO()
            Cooperation -> TODO()
            Disturbance -> TODO()
            Reverse2 -> TODO()
        }
        return@execute true
    }
}