package kr.mooner510.konopuro.domain.socket.data

object Protocol {
    object Match {
        const val ROOM_MATCH = 0
        const val ROOM_MATCH_FAILED = 1
        const val ROOM_MATCHED = 2
    }

    object Game {
        object Client {
            const val GAME_READY = 100
            const val ADD_CARD = 101
            const val USE_CARD = 102
            const val USE_ABILITY = 103
            const val SLEEP = 104
        }

        object Server {
            const val GAME_END = 150
            const val SUCCESS_CARD = 151
            const val SUCCESS_ABILITY = 152
            const val NEW_DAY = 153
            const val SLEEP = 154
            const val DATA_UPDATE = 155
        }
    }
}