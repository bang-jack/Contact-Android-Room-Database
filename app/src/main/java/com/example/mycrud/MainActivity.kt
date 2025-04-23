package com.example.mycrud

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycrud.adapter.UserAdapter
import com.example.mycrud.data.AppDatabase
import com.example.mycrud.data.entity.User
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var welcomeText: TextView
    private var list = mutableListOf<User>()
    private lateinit var adapter: UserAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        fab = findViewById(R.id.fab)
        welcomeText = findViewById(R.id.welcome_text)

        // Setup DB
        database = AppDatabase.getInstance(applicationContext)

        // Setup Adapter
        adapter = UserAdapter(list)
        adapter.setDialog(object : UserAdapter.Dialog {
            override fun onClick(position: Int) {
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle(list[position].fullname)
                    setItems(R.array.items_option) { dialog, which ->
                        when (which) {
                            0 -> {
                                val intent = Intent(this@MainActivity, EditorActivity::class.java)
                                intent.putExtra("id", list[position].id)
                                startActivity(intent)
                            }
                            1 -> {
                                database.userDao().delete(list[position])
                                getData()
                            }
                            else -> dialog.dismiss()
                        }
                    }
                    show()
                }
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )

        fab.setOnClickListener {
            startActivity(Intent(this, EditorActivity::class.java))
        }

        applyGradientToText()
        animateWelcomeText()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getData() {
        list.clear()
        list.addAll(database.userDao().getAll())
        adapter.notifyDataSetChanged()
    }

    private fun applyGradientToText() {
        val text = welcomeText.text.toString()
        val paint = welcomeText.paint
        val width = paint.measureText(text)

        val shader = LinearGradient(
            0f, 0f, width, welcomeText.textSize,
            intArrayOf(
                ContextCompat.getColor(this, android.R.color.holo_blue_light),
                ContextCompat.getColor(this, android.R.color.holo_purple)
            ),
            null,
            Shader.TileMode.CLAMP
        )
        welcomeText.paint.shader = shader
    }

    private fun animateWelcomeText() {
        welcomeText.alpha = 0f
        ObjectAnimator.ofFloat(welcomeText, "alpha", 0f, 1f).apply {
            duration = 1500
            start()
        }
    }
}

