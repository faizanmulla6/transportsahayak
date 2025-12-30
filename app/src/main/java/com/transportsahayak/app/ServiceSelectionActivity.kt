package com.transportsahayak.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.TextView

class ServiceSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_selection)

        // 1. Complaint Card -> Opens MainActivity
        findViewById<View>(R.id.cardComplaint).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // 2. RO Details Card -> Opens RODetailsActivity
        findViewById<View>(R.id.cardRODetails).setOnClickListener {
            startActivity(Intent(this, RODetailsActivity::class.java))
        }

        // 3. TREM Card -> Opens TremCardActivity
        findViewById<View>(R.id.cardTremCard).setOnClickListener {
            startActivity(Intent(this, TremCardActivity::class.java))
        }

        // 4. Feedback Card -> Opens FeedbackActivity
        findViewById<View>(R.id.cardFeedback).setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        // 5. Officer Login Text -> Opens AdminActivity
        // FIXED: ID changed to 'btnAdminLogin' to match your XML layout
        findViewById<View>(R.id.btnAdminLogin).setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
        }
    }
}