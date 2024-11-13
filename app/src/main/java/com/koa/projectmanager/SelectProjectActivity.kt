package com.koa.projectmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.koa.projectmanager.databinding.ActivitySelectProjectBinding

class ProjectAdapter(context: Context, projects: List<Project>) :ArrayAdapter<Project>(context, 0, projects) {
    private class ViewHolder(view: View) {
        val projectName: TextView = view.findViewById(R.id.tVprojectName)
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View
        if (convertView == null) {
            view = View.inflate(context, R.layout.project_layout, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val item = getItem(position)
        viewHolder.projectName.text = item?.projectName ?: "Unavailable"
        return view
        }
    }
class SelectProjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectProjectBinding
    private lateinit var adapter: ProjectAdapter
    private val projectList = mutableListOf<Project>()
    private val filteredProjectList = mutableListOf<Project>()

    override fun onCreate(savedInstanceState: Bundle?) {

        val user = intent.getParcelableExtra<FirebaseUser>("user")

        if (user != null ) {
            enableEdgeToEdge()
            super.onCreate(savedInstanceState)
            binding = ActivitySelectProjectBinding.inflate(layoutInflater)
            setContentView(binding.root)

            adapter = ProjectAdapter(this, filteredProjectList)
            binding.lVProjects.adapter = adapter

            val searchProjects: EditText = findViewById(R.id.edtSearchProjects)
            searchProjects.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Not used
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Not used
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterList(s.toString(), user)
                }
            })

            val projectRef = FirebaseDatabase.getInstance().reference.child("projectsInfo").child(user.uid)
            adapter = ProjectAdapter(this, filteredProjectList)
            val listView: ListView = findViewById(R.id.lVProjects)
            listView.adapter = adapter

            projectRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    projectList.clear()
                    for (projectSnapshot in dataSnapshot.children) {
                        val project = projectSnapshot.getValue(Project::class.java)
                        if (project != null) {
                            projectList.add(project)
                        }
                    }
                    projectList.sortBy { it.projectName }
                    filterList(searchProjects.text.toString(), user)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w("SELECT PROJECTS", "loadProject:onCancelled", error.toException())
                }
            })

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
    private fun filterList(query: String, user: FirebaseUser) {
        val filtered = if (query.isNotBlank()) {
            projectList.filter {
                it.projectName.contains(query, ignoreCase = true) ||
                        it.clientEmail.contains(query, ignoreCase = true)
            }
        } else {
            projectList
        }

        filteredProjectList.clear()
        filteredProjectList.addAll(filtered)
        filteredProjectList.sortBy { it.projectName }
        adapter.notifyDataSetChanged()

        val listView: ListView = findViewById(R.id.lVProjects)
        listView.setOnItemClickListener { _, _, position, _ ->
            val clickedItem = filteredProjectList[position]
            val intent = Intent(this, ViewProjectActivity::class.java)
            intent.putExtra("user", user)
            intent.putExtra("project", clickedItem)
            startActivity(intent)
        }
    }
}