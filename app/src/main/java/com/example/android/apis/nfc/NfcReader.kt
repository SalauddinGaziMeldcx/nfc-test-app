package com.example.android.apis.nfc

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.util.Log
import com.meldcx.android.nfc.model.*


const val CARD_TYPE_MIFARE_CLASSIC_1K = "Mifare S50 1K Card"
const val NFC_COMMAND_SUCCESSFUL = "Successful"

class NfcReader {

    private var readingNFCReadingConfig: List<NFCReadingConfig>? = null
    private var authenticationKeys: NFCKeys =
        NFCKeys(MifareClassic.KEY_DEFAULT, MifareClassic.KEY_DEFAULT)

    fun onTagDiscovered(tag: Tag): NFCResult {
        Log.d("ForegroundDispatch", "onTagDiscovered: id :  " + tag.id)
        val find: String? = tag.techList.find { it.equals(MifareClassic::class.java.name) }

        return if (find != null) {
            val mifareClassic: MifareClassic = MifareClassic.get(tag)
            readMifareCard(mifareClassic)
        } else {
            NFCResult.NFCResultError(4, "UnKnown TagTechnology ")
        }
    }

    private fun readMifareCard(mifareClassic: MifareClassic): NFCResult {
        val id = mifareClassic.tag.id.toHex(false)
        val type = when (mifareClassic.size) {
            MifareClassic.SIZE_1K -> CARD_TYPE_MIFARE_CLASSIC_1K
            else -> CARD_TYPE_MIFARE_CLASSIC_1K
        }

        Log.d(
            "ForegroundDispatch",
            "Id: $id, Type: ${mifareClassic.type}, Size: ${mifareClassic.size}"
        )
        mifareClassic.use { mi ->
            try {
                mi.connect()
                Log.d(
                    "ForegroundDispatch",
                    "is Connected: ${mi.isConnected} , Block count: ${mi.blockCount} , Sector count: ${mi.sectorCount}"
                )
                val nfcReading = ArrayList<MifareClassicReadingData>()
                val readingSectors = readingNFCReadingConfig?.let { configList ->
                    Array(configList.size) { configList[it].index }
                } ?: Array(mi.sectorCount) { it }
                val keys = readingNFCReadingConfig?.let { configList ->
                    Array(configList.size) {
                        configList[it].keys?.let { k -> k } ?: authenticationKeys
                    }
                } ?: Array(mi.sectorCount) { authenticationKeys }

                readingSectors.forEachIndexed { index, sector ->
                    val authKeys = keys[index]
                    authKeys.a?.let { mi.authenticateSectorWithKeyA(sector, it) }
                    authKeys.b?.let { mi.authenticateSectorWithKeyB(sector, it) }

                    val readingBlocks = readingNFCReadingConfig?.let { configList ->
                        configList.find { it.index == sector }?.let { config ->
                            config.blocks?.let { blocks ->
                                Array(blocks.size) { blocks[it].index }
                            } ?: Array(mi.getBlockCountInSector(sector)) { it }
                        }
                    } ?: Array(mi.getBlockCountInSector(sector)) { it }

                    val startBlock = mi.sectorToBlock(sector)
                    val contentIndex3 = mi.readBlock(startBlock + 3)
                    val access = ByteArray(4) { contentIndex3[it + 6] }.toUIntArray()
                    val blockData = Array(readingBlocks.size) { index ->
                        val blockIndex = readingBlocks[index]
                        val realBlock = startBlock + blockIndex
                        val blockContent = mi.readBlock(realBlock).toUIntArray()
                        NFCReadingBlock(blockIndex, blockContent)
                    }
                    nfcReading.add(
                        MifareClassicReadingData(
                            sector,
                            blockData,
                            NFCKeysReturn(authKeys.a?.toUIntArray(), authKeys.b?.toUIntArray()),
                            access
                        )
                    )
                }

                return NFCResult.MifareClassicResult(
                    id,
                    type,
                    mi.size,
                    nfcReading
                )
            } catch (e: Exception) {
                Log.d(
                    "ForegroundDispatch", "Error reading: ${e.message}"
                )
                return reportError(2, e.message)
            }
        }
    }


    private fun reportError(errorCode: Int, errorMessage: String?) =
        NFCResult.NFCResultError(errorCode, errorMessage)


    private fun readMifareUltraLightCard(mifareClassic: MifareUltralight): NFCResult {
        Log.d("ForegroundDispatch", "readMifareUltraLightCard")
        return reportError(5, "readMifareUltraLightCard")
    }
}

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()
fun ByteArray.toHex(separator: Boolean = true): String {
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
        if (separator)
            result.append(":")
    }

    return result.toString().substring(IntRange(0, result.toString().length - 2))
}

fun ByteArray.toUIntArray(): Array<Int> = Array(size) { get(it).toUByte().toInt() }
