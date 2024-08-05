package kr.mooner510.konopuro.domain.game.data.card.manager

import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.GamePreset
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
            PassiveType.CleanCode -> addFieldCard(DefaultCardType.CleanCode, 3, dupe = true, isDayDuration = true)
            PassiveType.Refectoring -> addFieldCard(DefaultCardType.Refectoring, 2, dupe = true, isDayDuration = true)
            PassiveType.JustRealize -> addFieldCard(DefaultCardType.JustRealize, 2, dupe = true, isDayDuration = true)
            PassiveType.IndustrialSpy -> otherModifier.execute { goal.keys.forEach { addProject(it, if(fieldCards.count() > 6) -2 else -1, false) } }
            else -> return@execute
        }
    }

    fun PlayerData.PlayerDataModifier.useTier(tierType: TierType) = execute {
        activeStudent = this.students.find { it.tiers.contains(tierType) }!!
        var useTime = tierType.time

        useTime -= get(DataKey.NovelTimeToday, 0)

        if (!removeTime(max(1, useTime))) return@execute false
        if (activeStudent.passives.contains(PassiveType.Mastering)) {
            otherModifier.execute {
                addProject(MajorType.iOS, -2, false)
                addProject(MajorType.Android, -2, false)
            }
        }
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

            TierType.IssueTracker -> addProject(
                MajorType.FrontEnd,
                9 + (issue[MajorType.FrontEnd]?.sum()?.coerceAtMost(20) ?: 0)
            )

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
            TierType.InfinityPassion -> {
                addProject(MajorType.iOS, 20)
            }
            TierType.DoItWithTime -> TODO()
            TierType.Reverse -> {
                addProject(MajorType.Android, 5)
                otherModifier.addProject(MajorType.Android, -5, false)
            }
            TierType.Cooperation -> {
                addProject(MajorType.Android, 6)
                if(students.count { it.groups.contains(MajorType.Android) } > 1) addTime(1)
            }
            TierType.Disturbance -> TODO()
            TierType.Reverse2 -> otherModifier.addProject(MajorType.Android, -12, false)
        }
        return@execute true
    }

    fun PlayerData.PlayerDataModifier.onNewDay() = execute {
        fieldCards.forEach {
            when (it.defaultCardType) {
                DefaultCardType.DawnCoding -> TODO()
                else -> return@forEach
            }
        }

        passives.forEach {
            when (it) {
                PassiveType.Novelist -> {
                    val timeUnit = if (passives.contains(PassiveType.ProNovelist) && time > 7) 2 else 1
                    time -= timeUnit
                    add(DataKey.NovelTimeTotal, timeUnit)
                    add(DataKey.NovelTimeToday, timeUnit)
                }

                PassiveType.RepeatMusic -> {
                    if (fieldCards.any { it.defaultCardType == DefaultCardType.Music }) {
                        addFieldCard(DefaultCardType.Music, 2, true, isDayDuration = true)
                    }
                }

                PassiveType.NightCoding -> if (time > 0) addFieldCard(
                    DefaultCardType.DawnCoding,
                    2,
                    true,
                    isDayDuration = true
                )

                PassiveType.ParallelProcess -> set(
                    DataKey.ParallelProcess,
                    if (get(DataKey.DesignProject) == null && get(DataKey.FrontEndProject) == null) 1 else 0
                )

                else -> return@forEach
            }
        }
    }

    fun PlayerData.PlayerDataModifier.onNewDayAfter() = execute {
        passives.forEach {
            when (it) {
                PassiveType.BlazePassion -> if (!(students.all { it.hasEndDate(StudentState.HalfLife) } || fieldCards.any { it.defaultCardType == DefaultCardType.Passion }) && students.any { it.getFatigue() > 1 }) {
                    addFieldCard(DefaultCardType.Passion, 3, true, isDayDuration = true)
                }

                PassiveType.MusicPlay -> {
                    addFieldCard(DefaultCardType.Music, 1, true, isDayDuration = true)
                    get(DataKey.SavedMusic)?.let {
                        repeat(it) {
                            addFieldCard(DefaultCardType.Music, 1, true, isDayDuration = true)
                        }
                    }
                    set(DataKey.SavedMusic, 0)
                }

                else -> return@forEach
            }
        }
    }

    fun PlayerData.PlayerDataModifier.calculateProject(majorType: MajorType, value: Int): Int = execute {
        var increment = 0
        fieldCards.forEach {
            when (it.defaultCardType) {
                DefaultCardType.Music -> increment++
                else -> return@forEach
            }
        }
        passives.forEach {
            when (it) {
                PassiveType.MultiDevelop -> increment += when (get(majorType.dataTotalKey, 0) / (goal[majorType]!!.toFloat())) {
                    0.1f -> 1
                    0.25f -> 2
                    0.5f -> 3
                    0.75f -> 4
                    else -> return@forEach
                }

                PassiveType.ParallelProcess ->
                    if (get(DataKey.ParallelProcess) != null && (majorType == MajorType.Design || majorType == MajorType.FrontEnd)
                    ) increment += 2

                PassiveType.IdeaDay ->
                    if (get(DataKey.IdeaDayCheck) == null && (majorType == MajorType.Design || majorType == MajorType.FrontEnd)) {
                        increment += 5
                        set(DataKey.IdeaDayCheck, 1)
                    }

                PassiveType.TimeSaving -> if (issue[majorType]?.isNotEmpty() == true) increment += 5
                PassiveType.Overload -> if (majorType == MajorType.FrontEnd && students.count {
                        it.groups.contains(
                            MajorType.FrontEnd
                        )
                    } > 1) increment += 4

                PassiveType.APIRequired -> if (majorType == MajorType.Backend && (get(
                        DataKey.BackendProjectTotal,
                        0
                    ) < get(DataKey.FrontEndProjectTotal, 0) * 0.8f)
                ) increment += 4

                else -> return@forEach
            }
        }
        otherModifier.execute other@{
            passives.forEach {
                when (it) {
                    PassiveType.Destore ->
                        if (majorType == MajorType.iOS || majorType == MajorType.Android) {
                            addProject(majorType, 2, false)
                        }

                    else -> return@forEach
                }
            }
        }
        return@execute increment + value
    }

    //current fatigue = 현재 학생 카드에 쌓여있는 피로도
    fun GameStudentCard.GameStudentCardModifier.increaseFatigue(value: Double, currentFatigue: Double): Double =
        execute {
            var applyValue = value;
            fieldCards.forEach {
                when (it.defaultCardType) {
                    else -> return@forEach
                }
            }
            passives.forEach {
                when (it) {
                    PassiveType.InfinityMusic -> {
                        val count = fieldCards.count { it.defaultCardType == DefaultCardType.Music }
                        applyValue -= value * (count.coerceAtMost(10) * .05f);
                    }

                    else -> return@forEach
                }
            }
            return@execute applyValue
        }

    fun GameStudentCard.GameStudentCardModifier.decreaseFatigue(value: Double, currentFatigue: Double): Double =
        execute {
            var applyValue = value;
            fieldCards.forEach {
                when (it.defaultCardType) {
                    else -> return@forEach
                }
            }
            passives.forEach {
                when (it) {
                    else -> return@forEach
                }
            }
            return@execute applyValue
        }
}
