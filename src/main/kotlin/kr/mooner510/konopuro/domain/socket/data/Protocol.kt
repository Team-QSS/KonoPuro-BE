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
            const val GAME_END = 200
            const val SUCCESS_CARD = 201
            const val SUCCESS_ABILITY = 202
            const val NEW_DAY = 203
            const val SLEEP = 204

            /**
             * 덱에서 서버가 카드 추가
             */
            const val NEW_CARD = 205

            /**
             * 패 업데이트
             */
            const val HELD_UPDATE = 206

            /**
             * 필드 업데이트
             */
            const val FIELD_UPDATE = 207

            /**
             * 프로젝트 진행도 업데이트
             */
            const val PROJECT_UPDATE = 208
        }
    }
}