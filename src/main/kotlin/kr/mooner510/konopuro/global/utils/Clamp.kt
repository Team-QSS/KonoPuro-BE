package kr.mooner510.konopuro.global.utils

import kotlin.math.max
import kotlin.math.min

object Clamp {
    fun clamp(value: Double, min: Double, max: Double) = min(max, max(min, value))
}
