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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.database
import java.io.Serializable

class AddProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val user = intent.getParcelableExtra<FirebaseUser>("user")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_project)

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
                val newProject = Project(projectName, clientEmail, dueDate, dueTime)

                val db = Firebase.database
                val projectsRef = db.reference.child("projectsInfo").child(user.uid).child(newProject.projectName)
                if (newProject.projectName.isNotEmpty() || newProject.clientEmail.isNotEmpty()) {
                    val projectData = mapOf(
                        "projectName" to newProject.projectName,
                        "clientEmail" to newProject.clientEmail,
                        "dueDate" to newProject.dueDate,
                        "dueTime" to newProject.dueTime
                    )
                    projectsRef.setValue(projectData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Project added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to add project", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
data class Project(
    var projectName: String = "",
    var clientEmail: String = "",
    var dueDate: String? = "",
    var dueTime: String? = ""
) : Serializable