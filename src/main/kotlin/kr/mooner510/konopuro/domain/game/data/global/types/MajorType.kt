package kr.mooner510.konopuro.domain.game.data.global.types

enum class MajorType {
    FrontEnd,
    Backend,
    Game,
    AI,
    Android,
    iOS,
    Embedded,
    DevOps,
    Design;

    companion object {
        fun majorGroup(vararg majors: MajorType): Long {
            return majors.sumOf { 1L shl it.ordinal }
        }
    }
}