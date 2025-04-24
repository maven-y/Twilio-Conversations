package com.twilio.conversations.app.data.model

import java.util.Date

enum class TransactionType {
    PURCHASE,
    CHAT,
    AUDIO_CALL,
    VIDEO_CALL,
    GIFT
}

data class PointsTransaction(
    val id: String,
    val userId: String,
    val transactionType: TransactionType,
    val points: Int,
    val referenceId: String?,
    val createdAt: Date
) 