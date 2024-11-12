package com.koa.projectmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseUser
import com.koa.projectmanager.databinding.ActivitySelectProjectBinding

class SelectProjectActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val user = intent.getParcelableExtra<FirebaseUser>("user")
        if (user != null ) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_select_project)

            val btnAddProject = findViewById<Button>(R.id.btnAddProject)
            btnAddProject.setOnClickListener {
                val intent = Intent(this, AddProjectActivity::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
            }
        } else {
            setContentView(R.layout.view_login)
            finish()
        }
    }
}