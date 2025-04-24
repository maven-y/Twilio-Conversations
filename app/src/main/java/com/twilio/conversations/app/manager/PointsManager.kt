package com.twilio.conversations.app.manager

import android.content.Context
import android.app.Activity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.twilio.conversations.app.data.model.PointsPackage
import com.twilio.conversations.app.data.model.TransactionType
import org.json.JSONObject
import timber.log.Timber

class PointsManager(private val context: Context) {
    private val checkout = Checkout()

    init {
        Checkout.preload(context)
        checkout.setKeyID("YOUR_RAZORPAY_KEY_ID")
    }

    fun purchasePoints(activity: Activity, packageId: String, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        val pointsPackage = PointsPackage.packages.find { pkg -> pkg.id == packageId }
            ?: throw IllegalArgumentException("Invalid package ID")

        val options = JSONObject().apply {
            put("name", "Dating App")
            put("description", "Purchase ${pointsPackage.points} Points")
            put("currency", "INR")
            put("amount", (pointsPackage.price * 100).toInt()) // Convert to paise
            put("prefill.email", "user@example.com")
            put("prefill.contact", "9876543210")
        }

        try {
            // Note: The activity passed here needs to implement PaymentResultListener
            // and handle onPaymentSuccess and onPaymentError callbacks
            checkout.open(activity, options)
        } catch (e: Exception) {
            Timber.e(e, "Error starting Razorpay checkout")
            onError("Failed to initialize payment: ${e.message}")
        }
    }

    fun usePointsForChat(messageId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Call backend API to deduct points for chat
        deductPoints(TransactionType.CHAT, 1, messageId, onSuccess, onError)
    }

    fun usePointsForAudioCall(callId: String, duration: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val points = calculateAudioCallPoints(duration)
        deductPoints(TransactionType.AUDIO_CALL, points, callId, onSuccess, onError)
    }

    fun usePointsForVideoCall(callId: String, duration: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val points = calculateVideoCallPoints(duration)
        deductPoints(TransactionType.VIDEO_CALL, points, callId, onSuccess, onError)
    }

    fun giftPoints(toUserId: String, points: Int, message: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Call backend API to transfer points
        deductPoints(TransactionType.GIFT, points, toUserId, onSuccess, onError)
    }

    private fun deductPoints(
        transactionType: TransactionType,
        points: Int,
        referenceId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Call backend API to deduct points
        // This should be implemented based on your backend API
        onSuccess()
    }

    private fun calculateAudioCallPoints(duration: Int): Int {
        val ratePerMinute = 10
        val minimumPoints = 20
        return maxOf(minimumPoints, (duration / 60) * ratePerMinute)
    }

    private fun calculateVideoCallPoints(duration: Int): Int {
        val ratePerMinute = 20
        val minimumPoints = 40
        return maxOf(minimumPoints, (duration / 60) * ratePerMinute)
    }
} 