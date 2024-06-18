package kr.mooner510.konopuro.domain.game.data.card.manager

import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.data.card.dto.GameStudentCard
import kr.mooner510.konopuro.domain.game.data.card.types.StudentState
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.socket.data.game.PlayerData
import kr.mooner510.konopuro.domain.socket.data.type.DataKey
import kotlin.math.max

object CardManager {
    fun PlayerData.PlayerDataModifier.useDefaultCard(defaultCardType: DefaultCardType): Unit = execute {
        defaultCardType.passives.forEach { usePassive(it) }
    }

    fun PlayerData.PlayerDataModifier.usePassive(passiveType: PassiveType): Unit = execute {
        when (passiveType) {
            PassiveType.ParallelProcess -> TODO()
            PassiveType.IdeaDay -> TODO()
            PassiveType.Overload -> TODO()
            PassiveType.APIRequired -> TODO()
            PassiveType.Novelist -> TODO()
            PassiveType.ProNovelist -> TODO()
            PassiveType.TimeSaving -> TODO()
            PassiveType.MusicPlay -> TODO()
            PassiveType.InfinityMusic -> TODO()
            PassiveType.RepeatMusic -> TODO()
            PassiveType.MultiDevelop -> TODO()
            PassiveType.FastAction -> TODO()
            PassiveType.NightCoding -> TODO()
            PassiveType.BlazePassion -> TODO()
            PassiveType.Mastering -> TODO()
            PassiveType.IssueCracker -> TODO()
            PassiveType.Destore -> TODO()
            PassiveType.Brocker -> TODO()
            PassiveType.Test -> addProject(MajorType.FrontEnd, 10)
        }
    }

    fun PlayerData.PlayerDataModifier.useTier(tierType: TierType) = execute {
        activeStudent = this.students.find { it.tiers.contains(tierType) }!!
        var useTime = tierType.time

        useTime -= get(DataKey.NovelTimeToday, 0)

        if (!removeTime(max(0, useTime))) return@execute false
        when (tierType) {
            TierType.Designer -> addProject(MajorType.Design, 6)
            TierType.Frontend -> addProject(MajorType.FrontEnd, 6)
            TierType.Backend -> addProject(MajorType.Backend, 6)
            TierType.iOS -> addProject(MajorType.iOS, 8)
            TierType.Android -> addProject(MajorType.Android, 8)
            TierType.MultiMajor ->
                if (isDone(MajorType.Design)) {
                    addProject(MajorType.FrontEnd, 7)
                } else {
                    addProject(MajorType.Design, 11)
                }

            TierType.SingleFocus ->
                if (get(DataKey.DesignProject, 0) > 0) {
                    addProject(MajorType.FrontEnd, 4)
                } else {
                    addProject(MajorType.FrontEnd, 5)
                }

            TierType.IssueComplete -> addProject(MajorType.FrontEnd, 4 + (issue[MajorType.FrontEnd]?.size ?: 0) * 3)
            TierType.SoleDesigner -> {
                addProject(MajorType.Design, 8)
                addFieldCard(DefaultCardType.OnlyPower, 3, isDayDuration = true)
            }

            TierType.GreatDesigner -> {
                addProject(MajorType.Design, 4)
                addFieldCard(DefaultCardType.UltimatePower, 3, dupe = true, isDayDuration = true)
            }

            TierType.IssueTracker -> addProject(MajorType.FrontEnd, 9 + (issue[MajorType.FrontEnd]?.sum()?.coerceAtMost(20) ?: 0))
            TierType.AutonomyStudy -> {
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

            TierType.EasyFirst -> {
                addProject(MajorType.FrontEnd, 22)
            }

            TierType.CustomNovelist -> {
                addProject(MajorType.FrontEnd, get(DataKey.NovelTimeTotal, 0))
            }

            TierType.AddBeat -> {
                addProject(MajorType.FrontEnd, 2)
                addProject(MajorType.Backend, 2)
                addFieldCard(DefaultCardType.Music, 1, dupe = true, isDayDuration = true)
            }

            TierType.BeatAddFE -> {
                addProject(MajorType.FrontEnd, 8)
                addFieldCard(DefaultCardType.Music, 1, dupe = true, isDayDuration = true)
            }

            TierType.BeatAddBE -> {
                addProject(MajorType.Backend, 8)
                addFieldCard(DefaultCardType.Music, 1, dupe = true, isDayDuration = true)
            }

            TierType.MusicFocus -> {
                if (project.getOrElse(MajorType.FrontEnd) { 0 } >= project.getOrElse(MajorType.Backend) { 0 }) {
                    if (time >= TierType.MusicFocus.time) addProject(
                        MajorType.Backend,
                        fieldCards.count { it.defaultCardType == DefaultCardType.Music } * 2)
                    else addProject(MajorType.Backend, 1)
                } else {
                    if (time >= TierType.MusicFocus.time) addProject(
                        MajorType.FrontEnd,
                        fieldCards.count { it.defaultCardType == DefaultCardType.Music } * 2)
                    else addProject(MajorType.FrontEnd, 1)
                }
            }

            TierType.DJ -> {
                set(DataKey.SavedMusic, fieldCards.count { it.defaultCardType == DefaultCardType.Music })
                removeFieldCard(DefaultCardType.Music)
            }

            TierType.RegularMeeting -> TODO()
            TierType.InfinityPassion -> TODO()
            TierType.DoItWithTime -> TODO()
            TierType.Reverse -> TODO()
            TierType.Cooperation -> TODO()
            TierType.Disturbance -> TODO()
            TierType.Reverse2 -> TODO()
        }
        return@execute true
    }

    fun PlayerData.PlayerDataModifier.onNewDay() = execute {
        if (passives.contains(PassiveType.Novelist)) {
            val timeUnit = if (passives.contains(PassiveType.ProNovelist) && time > 7) 2 else 1
            time -= timeUnit
            add(DataKey.NovelTimeTotal, timeUnit)
            add(DataKey.NovelTimeToday, timeUnit)
        }
        if (passives.contains(PassiveType.RepeatMusic)) {
            if (fieldCards.any { it.defaultCardType == DefaultCardType.Music }) {
                addFieldCard(DefaultCardType.Music, 2, true, isDayDuration = true)
            }
        }
    }

    fun PlayerData.PlayerDataModifier.onNewDayAfter() = execute {
        if (passives.contains(PassiveType.MusicPlay)) {
            addFieldCard(DefaultCardType.Music, 1, true, isDayDuration = true)
            get(DataKey.SavedMusic)?.let {
                repeat(it) {
                    addFieldCard(DefaultCardType.Music, 1, true, isDayDuration = true)
                }
            }
        }
    }

    fun PlayerData.PlayerDataModifier.calculateProject(majorType: MajorType): Int = execute {
        var increment = 0
        fieldCards.forEach {
            when (it.defaultCardType) {
                DefaultCardType.Music -> increment++
                else -> return@forEach
            }
        }
        if (issue[majorType]?.isNotEmpty() == true) increment += 5
        return@execute increment
    }

    fun GameStudentCard.GameStudentCardModifier.calculateFatigueAddition(): Double {
        var addition = 0.0
        // TODO: 피로도 증가 이벤트
        return addition
    }

    fun GameStudentCard.GameStudentCardModifier.calculateFatigueSubtraction(): Double {
        var subtraction = 0.0
        // TODO: 피로도 감소 이벤트
        return subtraction
    }
}
