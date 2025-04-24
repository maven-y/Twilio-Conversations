package com.twilio.conversations.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.twilio.conversations.app.data.model.PointsPackage
import com.twilio.conversations.app.databinding.ItemPointsPackageBinding
import java.text.NumberFormat
import java.util.Locale

class PointsPackageAdapter(
    private val onPackageSelected: (String) -> Unit
) : ListAdapter<PointsPackage, PointsPackageAdapter.PointsPackageViewHolder>(PointsPackageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointsPackageViewHolder {
        val binding = ItemPointsPackageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PointsPackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PointsPackageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PointsPackageViewHolder(
        private val binding: ItemPointsPackageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buyButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPackageSelected(getItem(position).id)
                }
            }
        }

        fun bind(pointsPackage: PointsPackage) {
            binding.packageName.text = "${pointsPackage.points} Points"
            binding.packagePrice.text = formatPrice(pointsPackage.price)
        }

        private fun formatPrice(price: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            return format.format(price)
        }
    }

    private class PointsPackageDiffCallback : DiffUtil.ItemCallback<PointsPackage>() {
        override fun areItemsTheSame(oldItem: PointsPackage, newItem: PointsPackage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PointsPackage, newItem: PointsPackage): Boolean {
            return oldItem == newItem
        }
    }
}