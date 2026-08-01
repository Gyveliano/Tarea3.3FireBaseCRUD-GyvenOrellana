package com.example.tarea33firebasecrud_gyvenorellana

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tarea33firebasecrud_gyvenorellana.adapter.PersonAdapter
import com.example.tarea33firebasecrud_gyvenorellana.firebase.FirebaseHelper
import com.example.tarea33firebasecrud_gyvenorellana.model.Personas

class PersonasActivity : AppCompatActivity(), PersonAdapter.OnItemClickListener {

    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etFecha: EditText
    private lateinit var etFoto: EditText
    private lateinit var btnSave: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnClear: Button
    private lateinit var rvPersons: RecyclerView

    private val helper = FirebaseHelper()
    private lateinit var adapter: PersonAdapter
    private var currentEditId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personas)

        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        etCorreo = findViewById(R.id.etCorreo)
        etFecha = findViewById(R.id.etFecha)
        etFoto = findViewById(R.id.etFoto)
        btnSave = findViewById(R.id.btnSave)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnClear = findViewById(R.id.btnClear)
        rvPersons = findViewById(R.id.rvPersons)

        adapter = PersonAdapter(mutableListOf(), this)
        rvPersons.layoutManager = LinearLayoutManager(this)
        rvPersons.adapter = adapter

        btnSave.setOnClickListener { savePerson() }
        btnUpdate.setOnClickListener { updatePerson() }
        btnClear.setOnClickListener { clearForm() }

        loadPersons()
    }

    private fun savePerson() {
        val p = Personas(
            nombres = etNombres.text.toString().trim(),
            apellidos = etApellidos.text.toString().trim(),
            correo = etCorreo.text.toString().trim(),
            fechanac = etFecha.text.toString().trim(),
            foto = etFoto.text.toString().trim()
        )
        if (p.nombres.isBlank() || p.apellidos.isBlank()) {
            Toast.makeText(this, "Nombres y apellidos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        helper.create(p) { ok, err ->
            runOnUiThread {
                if (ok) {
                    Toast.makeText(this, "Persona guardada", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    Toast.makeText(this, "Error: $err", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadPersons() {
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

    private fun updatePerson() {
        val id = currentEditId ?: return
        val p = Personas(
            id = id,
            nombres = etNombres.text.toString().trim(),
            apellidos = etApellidos.text.toString().trim(),
            correo = etCorreo.text.toString().trim(),
            fechanac = etFecha.text.toString().trim(),
            foto = etFoto.text.toString().trim()
        )
        helper.update(id, p) { ok, err ->
            runOnUiThread {
                if (ok) {
                    Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    Toast.makeText(this, "Error: $err", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun clearForm() {
        etNombres.setText("")
        etApellidos.setText("")
        etCorreo.setText("")
        etFecha.setText("")
        etFoto.setText("")
        currentEditId = null
        btnUpdate.visibility = View.GONE
        btnSave.visibility = View.VISIBLE
    }

    override fun onEdit(persona: Personas) {
        currentEditId = persona.id
        etNombres.setText(persona.nombres)
        etApellidos.setText(persona.apellidos)
        etCorreo.setText(persona.correo)
        etFecha.setText(persona.fechanac)
        etFoto.setText(persona.foto)
        btnSave.visibility = View.GONE
        btnUpdate.visibility = View.VISIBLE
    }

    override fun onDelete(persona: Personas) {
        helper.delete(persona.id) { ok, err ->
            runOnUiThread {
                if (ok) Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show() else Toast.makeText(this, "Error: $err", Toast.LENGTH_LONG).show()
            }
        }
    }
}

