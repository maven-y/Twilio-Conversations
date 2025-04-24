# Razorpay Integration Documentation

## Overview
This document outlines the implementation of a points-based system using Razorpay for the dating app. The system will handle:
- Points purchase through Razorpay
- Points usage for chat, audio calls, and video calls
- Points gifting between users
- Subscription management at the server level

## Points System

### Points Packages
```json
{
  "packages": [
    {
      "id": "points_100",
      "name": "100 Points",
      "points": 100,
      "price": 99,
      "razorpay_product_id": "prod_points_100"
    },
    {
      "id": "points_500",
      "name": "500 Points",
      "points": 500,
      "price": 399,
      "razorpay_product_id": "prod_points_500"
    },
    {
      "id": "points_1000",
      "name": "1000 Points",
      "points": 1000,
      "price": 699,
      "razorpay_product_id": "prod_points_1000"
    }
  ]
}
```

### Points Usage Rates
```json
{
  "rates": {
    "chat": {
      "per_message": 1,
      "per_media": 2
    },
    "audio_call": {
      "per_minute": 10,
      "minimum_points": 20
    },
    "video_call": {
      "per_minute": 20,
      "minimum_points": 40
    },
    "gift": {
      "minimum": 50,
      "maximum": 1000
    }
  }
}
```

## Server-Side Implementation

### Database Schema

#### Users Table
```sql
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    points_balance INT DEFAULT 0,
    subscription_status VARCHAR(20),
    subscription_end_date TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### Points Transactions Table
```sql
CREATE TABLE points_transactions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    transaction_type VARCHAR(20),
    points INT,
    reference_id VARCHAR(36),
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### Subscriptions Table
```sql
CREATE TABLE subscriptions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    plan_id VARCHAR(20),
    status VARCHAR(20),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### API Endpoints

#### Points Purchase
```http
POST /api/v1/points/purchase
Content-Type: application/json

{
  "package_id": "points_100",
  "payment_id": "razorpay_payment_id"
}
```

#### Points Usage
```http
POST /api/v1/points/use
Content-Type: application/json

{
  "user_id": "user_123",
  "transaction_type": "chat|audio_call|video_call|gift",
  "points": 10,
  "reference_id": "chat_id|call_id|gift_id"
}
```

#### Points Gift
```http
POST /api/v1/points/gift
Content-Type: application/json

{
  "from_user_id": "user_123",
  "to_user_id": "user_456",
  "points": 100,
  "message": "Happy Birthday!"
}
```

#### Subscription Management
```http
POST /api/v1/subscription/create
Content-Type: application/json

{
  "user_id": "user_123",
  "plan_id": "monthly_premium",
  "payment_id": "razorpay_payment_id"
}
```

## Android Implementation

### Dependencies
```gradle
dependencies {
    implementation 'com.razorpay:checkout:1.6.33'
    implementation 'com.razorpay:razorpay-android-sdk:1.6.33'
}
```

### Points Manager Class
```kotlin
class PointsManager(private val context: Context) {
    private val razorpay = Razorpay(context, "YOUR_KEY_ID")
    
    fun purchasePoints(packageId: String, amount: Int, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        val options = JSONObject().apply {
            put("name", "Dating App")
            put("description", "Points Purchase")
            put("currency", "INR")
            put("amount", amount * 100) // Amount in paise
            put("package_id", packageId)
        }
        
        razorpay.open(options, object : PaymentResultListener {
            override fun onPaymentSuccess(razorpayPaymentId: String?) {
                // Call backend API to credit points
                onSuccess(amount)
            }
            
            override fun onPaymentError(code: Int, response: String?) {
                onError(response ?: "Payment failed")
            }
        })
    }
}
```

### Usage in Chat/Calls
```kotlin
class ChatManager {
    private val pointsManager = PointsManager(context)
    
    fun sendMessage(message: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Check points balance
        if (userPoints >= 1) {
            // Send message
            // Deduct points
            onSuccess()
        } else {
            onError("Insufficient points")
        }
    }
    
    fun startAudioCall(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (userPoints >= 20) {
            // Start call
            // Deduct points
            onSuccess()
        } else {
            onError("Insufficient points")
        }
    }
}
```

## Security Considerations

1. **Payment Verification**
   - Verify payment signatures on server
   - Implement webhook for payment status updates
   - Store payment verification status

2. **Points Management**
   - Implement atomic transactions for points deduction
   - Use database transactions to prevent race conditions
   - Log all points transactions for audit

3. **Subscription Validation**
   - Validate subscription status on server
   - Implement subscription renewal logic
   - Handle subscription expiration

## Testing

1. **Unit Tests**
   - Points calculation
   - Subscription validation
   - Transaction processing

2. **Integration Tests**
   - Payment flow
   - Points deduction
   - Gift system

3. **End-to-End Tests**
   - Complete purchase flow
   - Chat with points
   - Call with points
   - Gift points

## Monitoring and Analytics

1. **Key Metrics**
   - Points purchase rate
   - Points usage patterns
   - Subscription conversion rate
   - Gift frequency

2. **Error Tracking**
   - Payment failures
   - Points deduction errors
   - Subscription issues

## Future Enhancements

1. **Points Features**
   - Points expiration
   - Points bonus system
   - Referral points
   - Daily rewards

2. **Subscription Features**
   - Family plans
   - Corporate subscriptions
   - Special offers
   - Trial periods

3. **Gift System**
   - Gift categories
   - Special occasion gifts
   - Gift messages
   - Gift notifications 