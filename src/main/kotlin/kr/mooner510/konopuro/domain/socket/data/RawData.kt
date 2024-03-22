package kr.mooner510.konopuro.domain.socket.data

data class RawData(
    var protocol: Int,
    var data: String
) {
    constructor() : this(0, "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawData

        if (protocol != other.protocol) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = protocol
        result = 31 * result + data.hashCode()
        return result
    }
}
