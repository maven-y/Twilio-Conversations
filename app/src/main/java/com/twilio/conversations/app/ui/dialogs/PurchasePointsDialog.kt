package com.twilio.conversations.app.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.twilio.conversations.app.adapter.PointsPackageAdapter
import com.twilio.conversations.app.data.model.PointsPackage
import com.twilio.conversations.app.databinding.DialogPurchasePointsBinding
import com.twilio.conversations.app.manager.PointsManager

class PurchasePointsDialog : BaseBottomSheetDialogFragment() {
    private lateinit var binding: DialogPurchasePointsBinding
    private lateinit var pointsManager: PointsManager
    private lateinit var adapter: PointsPackageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPurchasePointsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCloseButton()
    }

    private fun setupRecyclerView() {
        adapter = PointsPackageAdapter { packageId ->
            purchasePoints(packageId)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PurchasePointsDialog.adapter
        }
        adapter.submitList(PointsPackage.packages)
    }

    private fun setupCloseButton() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun purchasePoints(packageId: String) {
        activity?.let {
            pointsManager.purchasePoints(
                activity = it,
                packageId = packageId,
                onSuccess = { points ->
                    // Show success message
                    showSuccessMessage(points)
                    dismiss()
                },
                onError = { error ->
                    // Show error message
                    showErrorMessage(error)
                }
            )
        }
    }

    private fun showSuccessMessage(points: Int) {
        // Implement success message display
    }

    private fun showErrorMessage(error: String) {
        // Implement error message display
    }
} 