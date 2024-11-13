package com.koa.projectmanager

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ViewProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_project)

        val project = intent.getParcelableExtra<Project>("project")

        if (project != null) {
            val tvProjectName = findViewById<TextView>(R.id.tvProjectName)
            val tvClientEmail = findViewById<TextView>(R.id.tvClientEmail)
            val tvDueDate = findViewById<TextView>(R.id.tvDueDate)
            val tvStartDate = findViewById<TextView>(R.id.tvStartDate)
            val tvEndDate = findViewById<TextView>(R.id.tvEndDate)
            // ... (find other views as needed)

            tvProjectName.text = project.projectName
            tvClientEmail.text = project.clientEmail
            tvDueDate.text = "Due Date: ${project.dueDate}"
            tvStartDate.text = project.startDate
            tvEndDate.text = project.dueDate
            // ... (set values to other views as needed)

            // ... (rest of your code to set up the progress bar, timer, etc.)
        } else {
            // Handle the case where the project is not found in the intent
            // For example, you could show an error message or finish the activity
        }
    }
}