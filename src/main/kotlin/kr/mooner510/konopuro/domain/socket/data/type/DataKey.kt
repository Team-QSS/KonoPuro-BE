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
    SavedMusic, // 음악
    ParallelProcess, // 병행 프로세스 체크
    IdeaDayCheck, // 아이디어 샘솟는 날 체크
    ;

    companion object {
        val removals = entries.filter { it.removal }
    }
}
