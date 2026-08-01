package com.example.tarea33firebasecrud_gyvenorellana

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar listeners
        try {
            findViewById<Button>(R.id.btnManagePerson).setOnClickListener {
                startActivity(Intent(this, GestionarUsuariosActivity::class.java))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            findViewById<Button>(R.id.btnShowTable).setOnClickListener {
                startActivity(Intent(this, TableActivity::class.java))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            findViewById<Button>(R.id.btnExit).setOnClickListener {
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Edge to edge al final
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}