package kr.mooner510.konopuro.domain.game._preset

import kr.mooner510.konopuro.domain.game.data.global.types.MajorType

object GamePreset {
    val stage = listOf(
        mutableMapOf(
            MajorType.FrontEnd to 150,
            MajorType.Backend to 150,
            MajorType.Design to 80
        ),
        mutableMapOf(
            MajorType.FrontEnd to 10,
            MajorType.Backend to 10,
            MajorType.Design to 10,
            MajorType.Android to 10,
            MajorType.iOS to 10,
        ),
    )

    val gatchaOncePrice = 100
    val gatchaMultiPrice = 999
}
