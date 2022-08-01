package com.hand.portfolian.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatModel(
    val chatRoomId: String,
    val messageContent: String,
    val messageType: String,
    val sender: String,
    val receiver: String,
    val date: String
) : Parcelable