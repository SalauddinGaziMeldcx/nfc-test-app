package com.meldcx.android.nfc.model

sealed class NFCResult(val command: NFCMode, val returnCode: Int, val description: String?) {
    data class MifareClassicResult(
        val uid: String,
        val type: String,
        val capacity: Int,
        val sectors: List<MifareClassicReadingData>
    ) : NFCResult(NFCMode.read, 0, NFC_COMMAND_SUCCESSFUL)

    data class NFCResultError(@Transient val code: Int, @Transient val error: String?): NFCResult(NFCMode.read, code, error)
    object NFClose: NFCResult(NFCMode.close, 0, NFC_COMMAND_SUCCESSFUL)
    object ClientReady: NFCResult(NFCMode.ready, 0, NFC_COMMAND_SUCCESSFUL)
}

data class MifareClassicReadingData(val index: Int, val blocks: Array<NFCReadingBlock>, val keys: NFCKeysReturn?, val access: Array<Int>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MifareClassicReadingData

        if (index != other.index) return false
        if (!blocks.contentEquals(other.blocks)) return false
        if (keys != other.keys) return false
        if (!access.contentEquals(other.access)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + blocks.contentHashCode()
        result = 31 * result + (keys?.hashCode() ?: 0)
        result = 31 * result + access.contentHashCode()
        return result
    }
}