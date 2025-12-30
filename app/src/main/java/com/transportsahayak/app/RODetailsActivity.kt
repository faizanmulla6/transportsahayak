package com.transportsahayak.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

// Data Model
data class RetailOutletInfo(val name: String, val officerName: String, val phone: String, val dealerName: String, val lat: Double, val lng: Double)

class RODetailsActivity : AppCompatActivity() {

    private lateinit var etSearchRO: EditText
    private lateinit var btnSearch: Button
    private lateinit var cardROInfo: MaterialCardView
    private lateinit var tvROName: TextView
    private lateinit var tvOfficerName: TextView
    private lateinit var tvOfficerPhone: TextView
    private lateinit var tvDealerName: TextView
    private lateinit var btnGetDirections: Button

    // --- SAMPLE DATA ---
    private val outletList = listOf(
        RetailOutletInfo("Outlet A - Mumbai", "Officer A", "9999999999", "Dealer A", 19.0760, 72.8777),
        RetailOutletInfo("Outlet B - Pune", "Officer B", "8888888888", "Dealer B", 18.5204, 73.8567),
        // Add your full list here
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ro_details)

        etSearchRO = findViewById(R.id.etSearchRO)
        btnSearch = findViewById(R.id.btnSearch)
        cardROInfo = findViewById(R.id.cardROInfo)
        tvROName = findViewById(R.id.tvROName)
        tvOfficerName = findViewById(R.id.tvOfficerName)
        tvOfficerPhone = findViewById(R.id.tvOfficerPhone)
        tvDealerName = findViewById(R.id.tvDealerName)
        btnGetDirections = findViewById(R.id.btnGetDirections)

        // Hide card initially
        cardROInfo.visibility = View.GONE

        btnSearch.setOnClickListener {
            val query = etSearchRO.text.toString()
            if (query.isEmpty()) {
                Toast.makeText(this, "Enter RO Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Search logic (Simple contains check)
            val foundRO = outletList.find { it.name.contains(query, ignoreCase = true) }

            if (foundRO != null) {
                cardROInfo.visibility = View.VISIBLE
                tvROName.text = foundRO.name
                tvOfficerName.text = "Officer: ${foundRO.officerName}"
                tvOfficerPhone.text = "Phone: ${foundRO.phone}"
                tvDealerName.text = "Dealer: ${foundRO.dealerName}"

                btnGetDirections.setOnClickListener {
                    val uri = "google.navigation:q=${foundRO.lat},${foundRO.lng}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.google.android.apps.maps")
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Maps not installed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                cardROInfo.visibility = View.GONE
                Toast.makeText(this, "RO not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}