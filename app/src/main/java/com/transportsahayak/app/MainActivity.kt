package com.transportsahayak.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    // UI Elements
    private lateinit var actVehicleSearch: AutoCompleteTextView
    private lateinit var etDriverName: TextInputEditText
    private lateinit var autoCompleteOutlet: AutoCompleteTextView
    private lateinit var spinnerCategory: Spinner
    private lateinit var etDescription: TextInputEditText
    private lateinit var btnSubmit: Button
    private lateinit var btnAdminLogin: TextView

    // DATA from new project logic
    private val vehicles = mapOf(
        "MH12AB1234" to "ABC Transport",
        "MH12CD5678" to "XYZ Logistics",
        "MH14EF9012" to "PQR Movers"
    )

    private val outlets = listOf("Outlet A - Mumbai", "Outlet B - Pune", "Outlet C - Nashik")
    private val categories = listOf("Delay in Service", "Poor Quality", "Staff Behavior", "Safety Issue", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        // Bind Views using the IDs from your XML
        actVehicleSearch = findViewById(R.id.actVehicleSearch)
        etDriverName = findViewById(R.id.etDriverName)
        autoCompleteOutlet = findViewById(R.id.autoCompleteOutlet)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        etDescription = findViewById(R.id.etDescription)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnAdminLogin = findViewById(R.id.btnAdminLogin)

        setupSpinnersAndAdapters()
        setupVehicleSearchLogic()

        btnSubmit.setOnClickListener {
            submitComplaint()
        }

        // Optional: Admin Login click listener if you have AdminActivity
        btnAdminLogin.setOnClickListener {
            // startActivity(Intent(this, AdminActivity::class.java))
            Toast.makeText(this, "Admin Login Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSpinnersAndAdapters() {
        // Setup Outlet Search Adapter
        val outletAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, outlets)
        autoCompleteOutlet.setAdapter(outletAdapter)

        // Setup Category Spinner
        // Note: Using a custom spinner_item layout if you created one earlier, otherwise default
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = categoryAdapter

        // Setup Vehicle Search Adapter (Keys from map)
        val vehicleNumbers = vehicles.keys.toList()
        val vehicleAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, vehicleNumbers)
        actVehicleSearch.setAdapter(vehicleAdapter)
    }

    private fun setupVehicleSearchLogic() {
        // Logic: When user selects or types a vehicle number, check if it exists in map
        actVehicleSearch.setOnItemClickListener { parent, _, position, _ ->
            val selectedVehicle = parent.getItemAtPosition(position) as String
            val transporter = vehicles[selectedVehicle]
            if (transporter != null) {
                etDriverName.setText(transporter)
            }
        }

        // Optional: Also check as they type if exact match is found (for manual typing)
        // This acts as a backup to the dropdown selection
        actVehicleSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val input = s.toString().uppercase() // Ensure uppercase match
                if (vehicles.containsKey(input)) {
                    etDriverName.setText(vehicles[input])
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun submitComplaint() {
        val vehicleNumber = actVehicleSearch.text.toString()
        val transporterName = etDriverName.text.toString()
        val outlet = autoCompleteOutlet.text.toString()
        // Handle spinner selection safely
        val category = if (spinnerCategory.selectedItem != null) spinnerCategory.selectedItem.toString() else ""
        val description = etDescription.text.toString()

        // Validations
        if (vehicleNumber.isEmpty()) {
            actVehicleSearch.error = "Vehicle number required"
            return
        }
        if (transporterName.isEmpty()) {
            etDriverName.error = "Transporter Name required"
            return
        }
        if (outlet.isEmpty()) {
            autoCompleteOutlet.error = "Outlet required"
            return
        }

        // Disable button to prevent double-click
        btnSubmit.isEnabled = false
        btnSubmit.text = "Submitting..."

        val timestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateTime = dateFormat.format(Date(timestamp))

        val complaint = hashMapOf(
            "vehicleNumber" to vehicleNumber,
            "transporterName" to transporterName,
            "outlet" to outlet,
            "category" to category,
            "description" to description,
            "timestamp" to timestamp,
            "dateTime" to dateTime,
            "status" to "Pending"
        )

        db.collection("complaints")
            .add(complaint)
            .addOnSuccessListener {
                sendWhatsAppGroupMessage(vehicleNumber, transporterName, outlet, category, description, dateTime)
                Toast.makeText(this, "Complaint submitted successfully", Toast.LENGTH_SHORT).show()

                // Reset form or close app
                // finish() // Uncomment to close app

                // Re-enable button
                btnSubmit.isEnabled = true
                btnSubmit.text = getString(R.string.btn_send_whatsapp)

                // Clear fields
                actVehicleSearch.text.clear()
                etDriverName.text?.clear()
                autoCompleteOutlet.text.clear()
                etDescription.text?.clear()
            }
            .addOnFailureListener {
                btnSubmit.isEnabled = true
                btnSubmit.text = getString(R.string.btn_send_whatsapp)
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendWhatsAppGroupMessage(vehicle: String, transporter: String, outlet: String, category: String, desc: String, time: String) {
        // Attractive Message Format
        val message = """
            🚨 *Transport Sahayak - New Complaint* 🚨
            
            📍 *Location Details*
            ⛽ Outlet: $outlet
            
            🚛 *Transport Details*
            🚚 Vehicle: $vehicle
            👤 Transporter: $transporter
            
            ⚠️ *Issue Details*
            📌 Category: $category
            📝 Description: $desc
            
            📅 Time: $time
        """.trimIndent()

        // Use ACTION_SEND to share to groups
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp") // Target WhatsApp specifically
        intent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }
}