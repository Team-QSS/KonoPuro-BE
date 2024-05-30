package kr.mooner510.konopuro.global.utils

import kr.mooner510.konopuro.global.global.exception.InvalidUUIDException
import java.util.*

object UUIDParser {
    fun transfer(key: String): UUID {
        try {
            return UUID.fromString(key)
        } catch (e: Exception) {
            throw InvalidUUIDException(key)
        }
    }

    val nilUUID = UUID.fromString("00000000-0000-0000-0000-000000000000")!!
}