# Razorpay Implementation Guide for Chat, Audio & Video Call Points System

This guide provides detailed instructions on how to implement a points-based system using Razorpay for chat, audio calls, and video calls in your Android dating app.

## Table of Contents

1. [Project Setup](#project-setup)
2. [Points System Architecture](#points-system-architecture)
3. [Razorpay Integration](#razorpay-integration)
4. [Points Purchase Flow](#points-purchase-flow)
5. [Chat Integration](#chat-integration)
6. [Audio Call Integration](#audio-call-integration)
7. [Video Call Integration](#video-call-integration)
8. [Points Gifting](#points-gifting)
9. [Server-Side Implementation](#server-side-implementation)
10. [Troubleshooting](#troubleshooting)

## Project Setup

### Step 1: Add Razorpay Dependencies

Add the following to your app-level `build.gradle`:

```gradle
dependencies {
    // Razorpay SDK
    implementation 'com.razorpay:checkout:1.6.33'
    
    // Other dependencies
    // ...
}
```

### Step 2: Update AndroidManifest.xml

Add the required permissions and activities:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<application>
    <!-- ... other configurations ... -->
    
    <activity
        android:name="com.razorpay.CheckoutActivity"
        android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
        android:exported="false"
        android:theme="@style/AppTheme" />
    
    <!-- ... your other activities ... -->
</application>
```

## Points System Architecture

### Data Models

1. **PointsPackage.kt**
```kotlin
data class PointsPackage(
    val id: String,
    val name: String,
    val points: Int,
    val price: Double,
    val razorpayProductId: String
) {
    companion object {
        val packages = listOf(
            PointsPackage(
                id = "points_100",
                name = "100 Points",
                points = 100,
                price = 99.0,
                razorpayProductId = "prod_points_100"
            ),
            // More packages...
        )
    }
}
```

2. **PointsTransaction.kt**
```kotlin
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
```

### Manager Classes

**PointsManager.kt**
```kotlin
class PointsManager(private val context: Context) {
    private val checkout = Checkout()

    init {
        Checkout.preload(context)
        checkout.setKeyID("YOUR_RAZORPAY_KEY_ID")
    }

    fun purchasePoints(activity: Activity, packageId: String, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        // Implementation details...
    }

    fun usePointsForChat(messageId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Implementation details...
    }

    fun usePointsForAudioCall(callId: String, duration: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Implementation details...
    }

    fun usePointsForVideoCall(callId: String, duration: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Implementation details...
    }

    fun giftPoints(toUserId: String, points: Int, message: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Implementation details...
    }
}
```

## Razorpay Integration

### Step 1: Initialize Razorpay

Initialize Razorpay in your Application class:

```kotlin
class MyApplication : Application() {
    lateinit var pointsManager: PointsManager
    
    override fun onCreate() {
        super.onCreate()
        Checkout.preload(applicationContext)
        pointsManager = PointsManager(this)
    }
}
```

### Step 2: Implement PaymentResultListener

Create an activity that implements `PaymentResultListener`:

```kotlin
class PointsPurchaseActivity : AppCompatActivity(), PaymentResultListener {
    // Activity implementation...

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        // Handle successful payment
    }

    override fun onPaymentError(code: Int, description: String?) {
        // Handle payment error
    }
}
```

## Points Purchase Flow

### Step 1: Display Points Packages

Create a RecyclerView adapter for displaying points packages:

```kotlin
class PointsPackageAdapter(
    private val onPackageSelected: (String) -> Unit
) : ListAdapter<PointsPackage, PointsPackageAdapter.PointsPackageViewHolder>(PointsPackageDiffCallback()) {
    // Adapter implementation...
}
```

### Step 2: Handle Package Selection

When a user selects a package, initiate the Razorpay payment flow:

```kotlin
private fun purchasePoints(packageId: String) {
    pointsManager.purchasePoints(
        activity = this,
        packageId = packageId,
        onSuccess = { points ->
            // Update UI or navigate
        },
        onError = { error ->
            // Show error message
        }
    )
}
```

### Step 3: Process Payment Result

Handle the payment result in your activity:

```kotlin
override fun onPaymentSuccess(razorpayPaymentId: String?) {
    // 1. Call your backend API to verify payment
    // 2. Add points to user's account
    // 3. Show success UI
}

override fun onPaymentError(code: Int, description: String?) {
    // Show appropriate error message
}
```

## Chat Integration

### Step 1: Check Points Before Sending Message

```kotlin
fun sendMessage(message: String) {
    // Check if user has enough points
    if (userPoints < POINTS_PER_MESSAGE) {
        showInsufficientPointsDialog()
        return
    }
    
    // Proceed with sending message
    pointsManager.usePointsForChat(
        messageId = generateMessageId(),
        onSuccess = {
            // Send the message
            actualSendMessage(message)
        },
        onError = { error ->
            // Show error
            showError(error)
        }
    )
}
```

### Step 2: Update UI Based on Points Balance

```kotlin
fun updateChatUI() {
    // Enable/disable send button based on points
    binding.sendButton.isEnabled = userPoints >= POINTS_PER_MESSAGE
    
    // Show points balance
    binding.pointsBalanceText.text = "Points: $userPoints"
}
```

## Audio Call Integration

### Step 1: Check Points Before Starting Call

```kotlin
fun startAudioCall(userId: String) {
    val minimumPointsRequired = 20 // Minimum points needed for a call
    
    if (userPoints < minimumPointsRequired) {
        showInsufficientPointsDialog()
        return
    }
    
    // Start timer for call duration
    startCallDurationTimer()
    
    // Initialize call
    actualStartAudioCall(userId)
}
```

### Step 2: Handle Call End and Deduct Points

```kotlin
fun endAudioCall(callId: String, duration: Int) {
    pointsManager.usePointsForAudioCall(
        callId = callId,
        duration = duration,
        onSuccess = {
            // Update UI after points deduction
            updatePointsBalanceUI()
        },
        onError = { error ->
            // Handle error
            showError(error)
        }
    )
}
```

## Video Call Integration

### Step 1: Check Points Before Starting Call

```kotlin
fun startVideoCall(userId: String) {
    val minimumPointsRequired = 40 // Minimum points needed for a video call
    
    if (userPoints < minimumPointsRequired) {
        showInsufficientPointsDialog()
        return
    }
    
    // Start timer for call duration
    startCallDurationTimer()
    
    // Initialize call
    actualStartVideoCall(userId)
}
```

### Step 2: Handle Call End and Deduct Points

```kotlin
fun endVideoCall(callId: String, duration: Int) {
    pointsManager.usePointsForVideoCall(
        callId = callId,
        duration = duration,
        onSuccess = {
            // Update UI after points deduction
            updatePointsBalanceUI()
        },
        onError = { error ->
            // Handle error
            showError(error)
        }
    )
}
```

## Points Gifting

### Step 1: Create Gift Interface

```kotlin
fun showGiftDialog(userId: String) {
    val dialog = GiftPointsDialog(
        onGiftSent = { points, message ->
            sendGift(userId, points, message)
        }
    )
    dialog.show(supportFragmentManager, "gift_dialog")
}
```

### Step 2: Send Gift

```kotlin
fun sendGift(userId: String, points: Int, message: String) {
    if (userPoints < points) {
        showInsufficientPointsDialog()
        return
    }
    
    pointsManager.giftPoints(
        toUserId = userId,
        points = points,
        message = message,
        onSuccess = {
            // Show success message
            showGiftSentSuccessMessage()
            
            // Update UI
            updatePointsBalanceUI()
        },
        onError = { error ->
            // Handle error
            showError(error)
        }
    )
}
```

## Server-Side Implementation

### API Endpoints

Your backend should provide these endpoints:

1. **Verify Payment**
   ```
   POST /api/payments/verify
   {
     "razorpay_payment_id": "pay_123456789",
     "razorpay_order_id": "order_123456789",
     "razorpay_signature": "signature_string",
     "package_id": "points_100"
   }
   ```

2. **Get User Points**
   ```
   GET /api/users/{userId}/points
   ```

3. **Use Points**
   ```
   POST /api/points/use
   {
     "transaction_type": "CHAT|AUDIO_CALL|VIDEO_CALL|GIFT",
     "points": 10,
     "reference_id": "message_123|call_456"
   }
   ```

4. **Get Transaction History**
   ```
   GET /api/users/{userId}/transactions
   ```

### Webhook Integration

Set up Razorpay webhooks to receive payment status updates:

1. Configure webhook URL in Razorpay Dashboard
2. Implement webhook handler in your backend
3. Verify webhook signature for security
4. Process payment status updates

## Troubleshooting

### Common Issues and Solutions

1. **Razorpay Initialization Failed**
   - Check if you've added the correct dependencies
   - Ensure internet permissions are added to AndroidManifest.xml

2. **Payment Failed with Error Code 2**
   - This usually indicates network issues
   - Check device internet connection

3. **Points Not Added After Successful Payment**
   - Verify that your backend is correctly processing the payment verification
   - Check webhook configuration

4. **App Crashes During Payment**
   - Ensure your activity implements PaymentResultListener
   - Check if you're passing the correct parameters to Razorpay

### Debugging Tips

1. Enable Razorpay test mode during development
2. Use Razorpay test cards for payment testing
3. Implement comprehensive logging for payment flows
4. Monitor your backend logs for payment verification issues

### Testing with Test Cards

For testing payments without actual transactions, use these test cards in development:

| Card Network | Number           | CVV | Expiry Date |
|--------------|------------------|-----|-------------|
| Visa         | 4111 1111 1111 1111 | Any | Any        |
| Mastercard   | 5267 3181 8797 5449 | Any | Any        |
| RuPay        | 6051 9100 0000 0000 | Any | Any        |

Use any random Indian name and valid email to complete the test transactions.

## Best Practices

1. **Security**
   - Never store Razorpay keys in client code
   - Always verify payments on the server
   - Use HTTPS for all API communications

2. **User Experience**
   - Clearly display points pricing and usage rates
   - Show points balance prominently
   - Provide transaction history
   - Make purchase flow simple and quick

3. **Analytics**
   - Track purchase completion rates
   - Monitor points usage patterns
   - Analyze which features use most points
   - Optimize pricing based on usage data

---

This implementation guide covers the essential components for integrating Razorpay with a points-based system for chat, audio calls, and video calls. For further assistance, refer to the [Razorpay official documentation](https://razorpay.com/docs/). 