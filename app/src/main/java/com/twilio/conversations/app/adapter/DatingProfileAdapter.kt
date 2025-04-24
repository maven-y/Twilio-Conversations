package com.twilio.conversations.app.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twilio.conversations.app.R
import com.twilio.conversations.app.data.model.DatingProfile
import com.twilio.conversations.app.databinding.ItemDatingProfileCardBinding

class DatingProfileAdapter(
    private val onProfileClick: (DatingProfile) -> Unit
) : ListAdapter<DatingProfile, DatingProfileAdapter.ProfileViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dating_profile_card, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        private val nameText: TextView = itemView.findViewById(R.id.nameText)
        private val ageText: TextView = itemView.findViewById(R.id.ageText)
        private val locationText: TextView = itemView.findViewById(R.id.locationText)
        private val statusText: TextView = itemView.findViewById(R.id.statusText)
        private val verifiedBadge: ImageView = itemView.findViewById(R.id.verifiedIcon)
        private val onlineIndicator: ImageView = itemView.findViewById(R.id.onlineIndicator)
        private val languageText: TextView = itemView.findViewById(R.id.languageText)

        fun bind(profile: DatingProfile) {
            // Load profile image
            Glide.with(itemView.context)
                .load(profile.imageUrl)
                .centerCrop()
                .into(profileImage)

            // Set name
            nameText.text = profile.name

            // Randomly show either age or location
            val showAge = (adapterPosition % 2) == 0
            if (showAge) {
                ageText.visibility = View.VISIBLE
                locationText.visibility = View.GONE
                ageText.text = "${profile.age} years"
            } else {
                ageText.visibility = View.GONE
                locationText.visibility = View.VISIBLE
                locationText.text = profile.location
            }

            languageText.text = profile.language

            // Set status
            statusText.text = profile.statusText
            if (profile.statusText == "Online"){
                statusText.setTextColor(Color.WHITE)
                statusText.setBackgroundResource(R.drawable.bg_status_green)
            }

            onlineIndicator.visibility = if (profile.isOnline) View.VISIBLE else View.GONE

            // Set verified badge
            verifiedBadge.visibility = if (profile.isVerified) View.VISIBLE else View.GONE

            // Set click listener
            itemView.setOnClickListener { onProfileClick(profile) }
        }
    }

    private class ProfileDiffCallback : DiffUtil.ItemCallback<DatingProfile>() {
        override fun areItemsTheSame(oldItem: DatingProfile, newItem: DatingProfile): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DatingProfile, newItem: DatingProfile): Boolean {
            return oldItem == newItem
        }
    }
} 