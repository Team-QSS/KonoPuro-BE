package kr.mooner510.konopuro.domain.socket.data

data class RawChat(
    val protocol: Int,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawChat

        if (protocol != other.protocol) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = protocol.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
