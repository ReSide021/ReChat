package com.example.myapplication.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmInsertNewMsgDlg(
    val dialog_id: String,
    val sender: String,
    val typeMsg: String,
    val textMsg: String,
    val timeCreated: String,
    val receiverId: String,
    val nameSender: String
)