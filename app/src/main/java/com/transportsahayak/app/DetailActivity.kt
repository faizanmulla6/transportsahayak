package com.transportsahayak.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var tvDetailTitle: TextView
    private lateinit var lvDetails: ListView
    private val db = FirebaseFirestore.getInstance()

    // Data Model for the row
    data class ComplaintHistory(
        val date: Date? = null,
        val outlet: String = "",
        val category: String = "",
        val description: String = ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        tvDetailTitle = findViewById(R.id.tvDetailTitle)
        lvDetails = findViewById(R.id.lvDetails)

        val filterValue = intent.getStringExtra("FILTER_VALUE") ?: "All"
        tvDetailTitle.text = "HISTORY: $filterValue"

        loadHistory(filterValue)
    }

    private fun loadHistory(filterValue: String) {
        // Logic: If filterValue is a vehicle number, search vehicle. If outlet name, search outlet.
        // For simplicity, we just fetch all and filter in memory, or you can run specific queries.

        db.collection("complaints")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val historyList = ArrayList<ComplaintHistory>()
                for (document in result) {
                    val vehicle = document.getString("vehicle") ?: ""
                    val outlet = document.getString("outlet") ?: ""

                    // Check if this document matches the filter
                    // (Matches either vehicle OR outlet name)
                    if (vehicle == filterValue || outlet == filterValue || filterValue == "All") {
                        val c = document.toObject(ComplaintHistory::class.java)
                        historyList.add(c)
                    }
                }

                if (historyList.isEmpty()) {
                    Toast.makeText(this, "No history found", Toast.LENGTH_SHORT).show()
                }

                // Custom Adapter
                val adapter = object : ArrayAdapter<ComplaintHistory>(this, R.layout.item_history_row, historyList) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = convertView ?: layoutInflater.inflate(R.layout.item_history_row, parent, false)
                        val item = getItem(position)!!

                        val tvDate = view.findViewById<TextView>(R.id.tvHistoryDate)
                        val tvOutlet = view.findViewById<TextView>(R.id.tvHistoryOutlet)
                        val tvCategory = view.findViewById<TextView>(R.id.tvHistoryCategory)
                        val tvDesc = view.findViewById<TextView>(R.id.tvHistoryDescription)

                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        tvDate.text = if (item.date != null) "📅 ${dateFormat.format(item.date)}" else "N/A"
                        tvOutlet.text = item.outlet
                        tvCategory.text = item.category
                        tvDesc.text = item.description

                        return view
                    }
                }
                lvDetails.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show()
            }
    }
}