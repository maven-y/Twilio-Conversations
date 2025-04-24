package com.twilio.conversations.app.ui.dating

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.twilio.conversations.app.R
import com.twilio.conversations.app.adapter.DatingProfileAdapter
import com.twilio.conversations.app.data.model.DatingProfile
import com.twilio.conversations.app.databinding.ActivityDatingMainBinding
import com.twilio.conversations.app.ui.ConversationListActivity

class DatingMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDatingMainBinding
    private lateinit var profileAdapter: DatingProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatingMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadDummyProfiles()
    }

    private fun setupRecyclerView() {
        profileAdapter = DatingProfileAdapter(
            onProfileClick = { profile ->
                Toast.makeText(this, "Starting chat with ${profile.name}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ConversationListActivity::class.java))
            }
        )

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@DatingMainActivity, 2)
            adapter = profileAdapter
        }
    }

    private fun loadDummyProfiles() {
        profileAdapter.submitList(DatingProfile.getDummyProfiles())
    }
} 