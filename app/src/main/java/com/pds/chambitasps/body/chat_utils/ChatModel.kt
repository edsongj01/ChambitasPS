package com.pds.chambitasps.body.chat_utils

import com.google.firebase.Timestamp

data class ChatModel(
    val from: String,
    val message: String,
    val type: String,
)
