package com.transportsahayak.app

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminActivity : AppCompatActivity() {

    private lateinit var btnDateWise: Button
    private lateinit var btnVehicleWise: Button
    private lateinit var btnOutletWise: Button
    private lateinit var lvComplaints: ListView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        btnDateWise = findViewById(R.id.btnDateWise)
        btnVehicleWise = findViewById(R.id.btnVehicleWise)
        btnOutletWise = findViewById(R.id.btnOutletWise)
        lvComplaints = findViewById(R.id.lvComplaints)

        // Default load: Date Wise
        loadComplaints("Date")

        btnDateWise.setOnClickListener { loadComplaints("Date") }
        btnVehicleWise.setOnClickListener { loadComplaints("Vehicle") }
        btnOutletWise.setOnClickListener { loadComplaints("Outlet") }

        // Click listener for list items to go to Details
        lvComplaints.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String

            // Navigate to DetailActivity
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("FILTER_VALUE", selectedItem)
            startActivity(intent)
        }
    }

    private fun loadComplaints(filterType: String) {
        // Show loading
        Toast.makeText(this, "Loading $filterType report...", Toast.LENGTH_SHORT).show()

        db.collection("complaints")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val listItems = ArrayList<String>()

                for (document in result) {
                    val date = document.getDate("date")?.toString() ?: ""
                    val vehicle = document.getString("vehicle") ?: ""
                    val outlet = document.getString("outlet") ?: ""
                    val category = document.getString("category") ?: ""

                    when (filterType) {
                        "Date" -> listItems.add("$date - $outlet ($category)")
                        "Vehicle" -> if (!listItems.contains(vehicle)) listItems.add(vehicle) // Unique vehicles
                        "Outlet" -> if (!listItems.contains(outlet)) listItems.add(outlet)   // Unique outlets
                    }
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
                lvComplaints.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}