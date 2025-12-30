package com.transportsahayak.app

import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class FeedbackActivity : AppCompatActivity() {

    private lateinit var ratingBar: RatingBar
    private lateinit var etSuggestion: TextInputEditText
    private lateinit var btnSubmitFeedback: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        ratingBar = findViewById(R.id.ratingBar)
        etSuggestion = findViewById(R.id.etSuggestion)
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback)

        btnSubmitFeedback.setOnClickListener {
            val rating = ratingBar.rating
            val suggestion = etSuggestion.text.toString()

            if (rating == 0f) {
                Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val feedbackMap = hashMapOf(
                "rating" to rating,
                "suggestion" to suggestion,
                "date" to Date()
            )

            btnSubmitFeedback.isEnabled = false
            db.collection("feedback")
                .add(feedbackMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Feedback Submitted!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    btnSubmitFeedback.isEnabled = true
                    Toast.makeText(this, "Failed to submit", Toast.LENGTH_SHORT).show()
                }
        }
    }
}