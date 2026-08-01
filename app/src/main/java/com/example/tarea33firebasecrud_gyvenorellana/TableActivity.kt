package com.example.tarea33firebasecrud_gyvenorellana

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tarea33firebasecrud_gyvenorellana.adapter.PersonAdapter
import com.example.tarea33firebasecrud_gyvenorellana.firebase.FirebaseHelper
import com.example.tarea33firebasecrud_gyvenorellana.model.Personas

class TableActivity : AppCompatActivity(), PersonAdapter.OnItemClickListener {

    private lateinit var rvTable: RecyclerView
    private lateinit var adapter: PersonAdapter
    private val helper = FirebaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)
        rvTable = findViewById(R.id.rvTable)
        adapter = PersonAdapter(mutableListOf(), this)
        rvTable.layoutManager = LinearLayoutManager(this)
        rvTable.adapter = adapter

        loadTable()
    }

    private fun loadTable() {
        helper.readAll({ list ->
            runOnUiThread {
                adapter.setItems(list)
            }
        }, { error ->
            runOnUiThread {
                Toast.makeText(this, "Error lectura: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Reutilizamos los callbacks para edición/eliminación desde la tabla
    override fun onEdit(persona: Personas) {
        // abrir ManagePersonActivity con ID? Por simplicidad, copiar el ID al portapapeles
        // Pero aquí mostramos un Toast y el usuario puede ir a CRUD individual
        Toast.makeText(this, "ID para editar: ${persona.id}", Toast.LENGTH_SHORT).show()
    }

    override fun onDelete(persona: Personas) {
        helper.delete(persona.id) { ok, err ->
            runOnUiThread {
                if (ok) Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show() else Toast.makeText(this, "Error: $err", Toast.LENGTH_LONG).show()
            }
        }
    }
}

