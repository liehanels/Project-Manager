package com.koa.projectmanager

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.database

class AddProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_project)

        val user = intent.getParcelableExtra<FirebaseUser>("user")
        if (user != null) {
            val tVUserEmail = findViewById<TextView>(R.id.tVUserEmail)
            tVUserEmail.text = user.email

            val eTProjectName = findViewById<EditText>(R.id.eTProjectName)
            val eTClientEmail = findViewById<EditText>(R.id.eTClientEmail)
            val eTDueDate = findViewById<EditText>(R.id.eTDueDate)
            val eTDueTime = findViewById<EditText>(R.id.eTDueTime)

            val btnConfirmAddProject = findViewById<Button>(R.id.btnConfirmAddProject)
            btnConfirmAddProject.setOnClickListener {
                val projectName = eTProjectName.text.toString()
                val clientEmail = eTClientEmail.text.toString()
                val dueDate = eTDueDate.text.toString()
                val dueTime = eTDueTime.text.toString()
                val newProject = Project(user.email!!, projectName, clientEmail, dueDate, dueTime)
                val db = Firebase.database
                val projectsRef = db.getReference("projectsInfo")
                if (newProject.projectName.isNotEmpty() || newProject.clientEmail.isNotEmpty() || newProject.userEmail.isNotEmpty()) {
                    projectsRef.push().setValue(newProject)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Project added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                } else {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
data class Project(
    var userEmail: String,
    var projectName: String,
    var clientEmail: String,
    var dueDate: String?,
    var dueTime: String?
)