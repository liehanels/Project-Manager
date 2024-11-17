package com.koa.projectmanager

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.Locale

class ViewProjectActivity : AppCompatActivity() {

    private lateinit var chronometer: Chronometer
    private lateinit var btnStartTimer: Button
    private lateinit var btnEndTimer: Button
    private lateinit var btnDeleteProject: Button
    private lateinit var tvTimeWorked: TextView
    private lateinit var database: FirebaseDatabase
    private var startTime: Long = 0
    private var totalTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_project)

        database = FirebaseDatabase.getInstance()
        var project = intent.getParcelableExtra<Project>("project")
        var user = intent.getParcelableExtra<FirebaseUser>("user")

        if (project != null && user != null) {
            val tvProjectName = findViewById<TextView>(R.id.tvProjectName)
            val tvClientEmail = findViewById<TextView>(R.id.tvClientEmail)
            val tvDueDate = findViewById<TextView>(R.id.tvDueDate)

            tvProjectName.text = project.projectName
            tvClientEmail.text = project.clientEmail
            tvDueDate.text = "Due Date: ${project.dueDate}"

            chronometer = findViewById(R.id.chronometerTimeWorked)
            btnStartTimer = findViewById(R.id.btnStartTimer)
            btnEndTimer = findViewById(R.id.btnEndTimer)
            btnDeleteProject = findViewById(R.id.btnDeleteProject)
            tvTimeWorked = findViewById(R.id.tvTimeWorked)

            val db = Firebase.database
            val projectsRef = db.reference.child("projectsInfo").child(user.uid).child(project.projectName)

            projectsRef.child("timeSpent").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    totalTime = snapshot.value.toString().toLongOrNull() ?: 0
                    updateTvTimeWorked(totalTime)
                    Log.d("FIREBASE", "Time spent: $totalTime")
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FIREBASE", "Error reading time spent: ${error.message}")
                }
            })

            btnStartTimer.setOnClickListener {
                startTimer()
            }

            btnEndTimer.setOnClickListener {
                stopTimer(user, project)
            }

            btnDeleteProject.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirm Delete")
                builder.setMessage("Are you sure you want to delete this project?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    deleteProject(user, project)
                    val intent = Intent(this, SelectProjectActivity::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                }
                builder.setNegativeButton("No") { dialog, which ->
                    // Do nothing
                }
                builder.show()
            }
        } else {
            Log.e("PROJECT", "No project found")
            Toast.makeText(this, "Error loading project", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer() {
        startTime = SystemClock.elapsedRealtime()
        chronometer.base = startTime
        chronometer.start()
        chronometer.visibility = View.VISIBLE

        btnStartTimer.visibility = View.GONE
        btnEndTimer.visibility = View.VISIBLE
    }

    private fun stopTimer(user: FirebaseUser, project: Project) {
        chronometer.stop()
        val endTime = SystemClock.elapsedRealtime()
        val elapsedMillis = endTime - startTime

        totalTime += elapsedMillis

        updateTvTimeWorked(totalTime)

        val db = Firebase.database
        val projectsRef = db.reference.child("projectsInfo").child(user.uid).child(project.projectName)

        val updates = hashMapOf<String, Any>(
            "timeSpent" to totalTime.toString()
        )
        projectsRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("FIREBASE", "Time spent updated successfully")
            }
            .addOnFailureListener {
                Log.e("FIREBASE", "Error updating time spent: ${it.message}")
            }

        btnStartTimer.visibility = View.VISIBLE
        btnEndTimer.visibility = View.GONE
    }

    private fun updateTvTimeWorked(totalTimeInMillis: Long) {
        val totalSeconds = totalTimeInMillis / 1000
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60

        tvTimeWorked.text = String.format(Locale.US,
            "Time Worked: %02dh %02dm %02ds",
            totalHours,
            totalMinutes % 60,
            totalSeconds % 60
        )
    }

    private fun deleteProject(user: FirebaseUser, project: Project) {
        val db = Firebase.database
        val projectRef = db.reference.child("projectsInfo").child(user.uid).child(project.projectName)

        projectRef.removeValue()
            .addOnSuccessListener {
                Log.d("FIREBASE", "Project deleted successfully")

                Toast.makeText(this, "Project deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Log.e("FIREBASE", "Error deleting project: ${it.message}")
                Toast.makeText(this, "Error deleting project", Toast.LENGTH_SHORT).show()
            }
    }
}