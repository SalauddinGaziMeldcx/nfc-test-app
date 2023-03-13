package com.meldcx.android.nfc.model

data class NFCCommand(val command: NFCMode, val parameters: NFCReadingParameter?)

data class NFCReadingParameter(val config: List<NFCReadingConfig>?, val keys: NFCKeys?, val options: NFCReadingOptions?)

data class NFCReadingConfig(val index: Int, val blocks: List<NFCReadingBlock>?, val keys: NFCKeys?)

data class NFCKeys(val a: ByteArray?, val b: ByteArray?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NFCKeys

        if (a != null) {
            if (other.a == null) return false
            if (!a.contentEquals(other.a)) return false
        } else if (other.a != null) return false
        if (b != null) {
            if (other.b == null) return false
            if (!b.contentEquals(other.b)) return false
        } else if (other.b != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = a?.contentHashCode() ?: 0
        result = 31 * result + (b?.contentHashCode() ?: 0)
        return result
    }
}

data class NFCReadingBlock(val index: Int, val data: Array<Int>?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NFCReadingBlock

        if (index != other.index) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}

data class NFCReadingOptions(val cardType: String)

data class NFCKeysReturn(val a: Array<Int>?, val b: Array<Int>?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NFCKeysReturn

        if (!a.contentEquals(other.a)) return false
        if (!b.contentEquals(other.b)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = a.contentHashCode()
        result = 31 * result + b.contentHashCode()
        return result
    }
}

enum class NFCMode {
    read, write, close, ready
}
