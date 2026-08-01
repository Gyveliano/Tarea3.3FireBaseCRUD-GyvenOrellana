package com.example.tarea33firebasecrud_gyvenorellana

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tarea33firebasecrud_gyvenorellana.firebase.FirebaseHelper
import com.example.tarea33firebasecrud_gyvenorellana.model.Personas
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ManagePersonActivity : AppCompatActivity() {

    private lateinit var etId: EditText
    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etFecha: EditText
    private lateinit var etFoto: EditText
    private lateinit var btnCreate: Button
    private lateinit var btnRead: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private val helper = FirebaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_person)

        etId = findViewById(R.id.etId)
        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        etCorreo = findViewById(R.id.etCorreo)
        etFecha = findViewById(R.id.etFecha)
        etFoto = findViewById(R.id.etFoto)
        btnCreate = findViewById(R.id.btnCreate)
        btnRead = findViewById(R.id.btnRead)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)

        btnCreate.setOnClickListener { createPerson() }
        btnRead.setOnClickListener { readPerson() }
        btnUpdate.setOnClickListener { updatePerson() }
        btnDelete.setOnClickListener { deletePerson() }
    }

    private fun createPerson() {
        val p = Personas(
            nombres = etNombres.text.toString().trim(),
            apellidos = etApellidos.text.toString().trim(),
            correo = etCorreo.text.toString().trim(),
            fechanac = etFecha.text.toString().trim(),
            foto = etFoto.text.toString().trim()
        )
        if (p.nombres.isBlank() || p.apellidos.isBlank()) {
            Toast.makeText(this, "Nombres y apellidos requeridos", Toast.LENGTH_SHORT).show()
            return
        }
        helper.create(p) { ok, err ->
            runOnUiThread {
                if (ok) {
                    Toast.makeText(this, "Creado con ID: ${p.id}", Toast.LENGTH_SHORT).show()
                    etId.setText(p.id)
                } else Toast.makeText(this, "Error: $err", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun readPerson() {
        val id = etId.text.toString().trim()
        if (id.isBlank()) {
            Toast.makeText(this, "Proporciona un ID para buscar", Toast.LENGTH_SHORT).show()
            return
        }
        // leer nodo específico
        val ref = com.google.firebase.database.FirebaseDatabase.getInstance().reference.child("personas").child(id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val p = snapshot.getValue(Personas::class.java)
                if (p != null) {
                    etNombres.setText(p.nombres)
                    etApellidos.setText(p.apellidos)
                    etCorreo.setText(p.correo)
                    etFecha.setText(p.fechanac)
                    etFoto.setText(p.foto)
                    Toast.makeText(this@ManagePersonActivity, "Encontrado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ManagePersonActivity, "No encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManagePersonActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updatePerson() {
        val id = etId.text.toString().trim()
        if (id.isBlank()) {
            Toast.makeText(this, "Proporciona un ID para actualizar", Toast.LENGTH_SHORT).show()
            return
        }
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
                if (ok) Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show() else Toast.makeText(this, "Error: $err", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deletePerson() {
        val id = etId.text.toString().trim()
        if (id.isBlank()) {
            Toast.makeText(this, "Proporciona un ID para borrar", Toast.LENGTH_SHORT).show()
            return
        }
        helper.delete(id) { ok, err ->
            runOnUiThread {
                if (ok) {
                    Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                    // limpiar campos
                    etId.setText("")
                    etNombres.setText("")
                    etApellidos.setText("")
                    etCorreo.setText("")
                    etFecha.setText("")
                    etFoto.setText("")
                } else Toast.makeText(this, "Error: $err", Toast.LENGTH_LONG).show()
            }
        }
    }
}

