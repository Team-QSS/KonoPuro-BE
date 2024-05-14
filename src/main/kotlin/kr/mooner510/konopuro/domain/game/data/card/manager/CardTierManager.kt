package kr.mooner510.konopuro.domain.game.data.card.manager

import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game._preset.TierType.*
import kr.mooner510.konopuro.domain.game.data.global.types.MajorType
import kr.mooner510.konopuro.domain.socket.data.game.PlayerData

object CardTierManager {
    fun PlayerData.PlayerDataModifier.useTier(tierType: TierType) = execute {
        activeStudent = this.students.find { it.tiers.contains(tierType) }!!
        when (tierType) {
            Designer -> removeTime(3) { addProject(MajorType.Design, 6) }
            Frontend -> removeTime(3) { addProject(MajorType.FrontEnd, 6) }
            Backend -> removeTime(3) { addProject(MajorType.Backend, 6) }
            iOS -> removeTime(4) { addProject(MajorType.iOS, 8) }
            Android -> removeTime(4) { addProject(MajorType.Android, 8) }

            MultiMajor -> removeTime(5) {
                if (isDone(MajorType.Design)) {
                    addProject(MajorType.Design, 11)
                } else {
                    addProject(MajorType.FrontEnd, 7)
                }
            }

            SingleFocus -> removeTime(2) {
                if (todayLog.any { it.player == id && it.majorType == MajorType.Design && it.amount > 0 }) {
                    addProject(MajorType.FrontEnd, 4)
                } else {
                    addProject(MajorType.FrontEnd, 5)
                }
            }

            IssueComplete -> removeTime(4) {
                addProject(MajorType.FrontEnd, 4 + (issue[MajorType.FrontEnd]?.size ?: 0) * 3)
            }

            SoleDesigner -> removeTime(10) {
                addProject(MajorType.Design, 10)
            }

            GreatDesigner -> TODO()
            IssueTracker -> TODO()
            AutonomyStudy -> TODO()
            EasyFirst -> TODO()
            CustomNovelist -> TODO()
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
    }
}