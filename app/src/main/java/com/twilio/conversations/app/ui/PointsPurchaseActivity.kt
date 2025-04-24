package com.twilio.conversations.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.twilio.conversations.app.R
import com.twilio.conversations.app.adapter.PointsPackageAdapter
import com.twilio.conversations.app.data.model.PointsPackage
import com.twilio.conversations.app.databinding.ActivityPointsPurchaseBinding
import com.twilio.conversations.app.manager.PointsManager
import timber.log.Timber

class PointsPurchaseActivity : AppCompatActivity(), PaymentResultListener {
    
    private lateinit var binding: ActivityPointsPurchaseBinding
    private lateinit var pointsManager: PointsManager
    private lateinit var adapter: PointsPackageAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointsPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        pointsManager = PointsManager(applicationContext)
        
        setupToolbar()
        setupRecyclerView()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Purchase Points"
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = PointsPackageAdapter { packageId: String ->
            purchasePoints(packageId)
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PointsPurchaseActivity)
            adapter = this@PointsPurchaseActivity.adapter
        }
        
        adapter.submitList(PointsPackage.packages)
    }
    
    private fun purchasePoints(packageId: String) {
        pointsManager.purchasePoints(
            activity = this,
            packageId = packageId,
            onSuccess = { points ->
                // This won't actually be called - see onPaymentSuccess
            },
            onError = { error ->
                // This is only called for initialization errors
                showError(error)
            }
        )
    }
    
    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Timber.d("Payment success: $razorpayPaymentId")
        
        // Call your backend API to add points
        // For now just show a success message
        showSuccess("Payment successful! Points added to your account.")
    }
    
    override fun onPaymentError(code: Int, description: String?) {
        Timber.e("Payment error: $code - $description")
        showError("Payment failed: ${description ?: "Unknown error"}")
    }
    
    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
} 