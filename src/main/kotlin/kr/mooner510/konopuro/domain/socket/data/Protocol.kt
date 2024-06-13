package kr.mooner510.konopuro.domain.socket.data

object Protocol {
    object Match {
        const val ROOM_MATCH = 0
        const val ROOM_MATCH_FAILED = 1
        const val ROOM_MATCHED = 2
        const val DISCONNECTED = 3
        const val RECONNECTED = 4
    }

    object Game {
        object Client {
            const val GAME_READY = 100

            /**
             * 덱에서 카드 추가
             */
            const val ADD_CARD = 101

            /**
             * 상대가 카드 추가
             */
            const val ADD_CARD_OTHER = 102

            /**
             * 카드 사용
             */
            const val USE_CARD = 103

            /**
             * 카드 능력 사용
             */
            const val USE_ABILITY = 104

            /**
             * 수면
             */
            const val SLEEP = 105
        }

        object Server {
            const val GAME_START = 200
            const val GAME_END = 201
            const val SUCCESS_CARD = 202
            const val SUCCESS_ABILITY = 203
            const val NEW_DAY = 204
            const val DATA_UPDATE = 205

//            const val OTHER_SLEEP = 205

            /**
             * 덱에서 서버가 카드 추가
             */
//            const val NEW_CARD = 207
        }
    }
}
