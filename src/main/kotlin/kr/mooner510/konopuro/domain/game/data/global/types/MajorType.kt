package kr.mooner510.konopuro.domain.game.data.global.types

import kr.mooner510.konopuro.domain.socket.data.type.DataKey

enum class MajorType(
    val dataKey: DataKey,
    val dataTotalKey: DataKey
) {
    FrontEnd(DataKey.FrontEndProject, DataKey.FrontEndProjectTotal),
    Backend(DataKey.BackendProject, DataKey.BackendProjectTotal),
    Game(DataKey.GameProject, DataKey.GameProjectTotal),
    AI(DataKey.AIProject, DataKey.AIProjectTotal),
    Android(DataKey.AndroidProject, DataKey.AndroidProjectTotal),
    iOS(DataKey.iOSProject, DataKey.iOSProjectTotal),
    Embedded(DataKey.EmbeddedProject, DataKey.EmbeddedProjectTotal),
    DevOps(DataKey.DevOpsProject, DataKey.DevOpsProjectTotal),
    Design(DataKey.DesignProject, DataKey.DesignProjectTotal)
    ;

    companion object {
        fun majorGroup(vararg majors: MajorType): Long {
            return majors.sumOf { 1L shl it.ordinal }
        }
    }
}