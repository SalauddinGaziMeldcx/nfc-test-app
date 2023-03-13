package com.meldcx.android.nfc.model

data class NFCTechData(val tech: List<NFCTech>)

enum class NFCTech {
   IsoDep, NfcA, NfcB, NfcF,NfcV, Ndef, NdefFormatable, MifareClassic, MifareUltralight
}

const val CARD_TYPE_MIFARE_CLASSIC_1K = "Mifare S50 1K Card"
const val NFC_COMMAND_SUCCESSFUL = "Successful"