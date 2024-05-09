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
            MajorType.FrontEnd to 120,
            MajorType.Backend to 200,
            MajorType.Design to 180,
            MajorType.Android to 120,
            MajorType.iOS to 120,
        ),
    )
}