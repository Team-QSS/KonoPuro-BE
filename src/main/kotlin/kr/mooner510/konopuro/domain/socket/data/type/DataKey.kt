package kr.mooner510.konopuro.domain.socket.data.type

enum class DataKey(
    private val removal: Boolean = false
) {
    FrontEndProject(true),
    BackendProject(true),
    GameProject(true),
    AIProject(true),
    AndroidProject(true),
    iOSProject(true),
    EmbeddedProject(true),
    DevOpsProject(true),
    DesignProject(true),
    FrontEndProjectTotal,
    BackendProjectTotal,
    GameProjectTotal,
    AIProjectTotal,
    AndroidProjectTotal,
    iOSProjectTotal,
    EmbeddedProjectTotal,
    DevOpsProjectTotal,
    DesignProjectTotal,


    NovelTimeTotal, // 소설 시간
    NovelTimeToday(true), // 소설 시간
    Music, // 음악
    ;

    companion object {
        val removals = entries.filter { it.removal }
    }
}
