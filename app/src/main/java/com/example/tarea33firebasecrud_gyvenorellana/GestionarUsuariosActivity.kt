package com.example.tarea33firebasecrud_gyvenorellana

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tarea33firebasecrud_gyvenorellana.firebase.FirebaseHelper
import com.example.tarea33firebasecrud_gyvenorellana.model.Personas

class GestionarUsuariosActivity : AppCompatActivity() {

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
    private lateinit var spinnerIds: Spinner

    private val personsList = mutableListOf<Personas>()


    private val helper = FirebaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_person)

        spinnerIds = findViewById(R.id.spinnerIds)
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

        // Poblamos el spinner con los IDs disponibles
        populateSpinner()

        btnCreate.setOnClickListener { createPerson() }
        btnRead.setOnClickListener { readPerson() }
        btnUpdate.setOnClickListener { updatePerson() }
        btnDelete.setOnClickListener { deletePerson() }
    }

    private fun populateSpinner() {
        helper.readAll({ list ->
            runOnUiThread {
                personsList.clear()
                personsList.addAll(list)
                val ids = mutableListOf<String>()
                ids.add("Seleccione un ID")
                ids.addAll(list.map { it.id })
                val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ids) {
                    override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                        val view = super.getView(position, convertView, parent) as android.widget.TextView
                        view.setTextColor(Color.parseColor("#000000"))
                        return view
                    }

                    override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                        val view = super.getDropDownView(position, convertView, parent) as android.widget.TextView
                        view.setTextColor(Color.parseColor("#000000"))
                        return view
                    }
                }
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerIds.adapter = adapter
                spinnerIds.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                        if (position <= 0) return
                        val selectedId = ids[position]
                        // rellenar campos con el objeto correspondiente
                        val p = personsList.find { it.id == selectedId }
                        if (p != null) {
                            etId.setText(p.id)
                            etNombres.setText(p.nombres)
                            etApellidos.setText(p.apellidos)
                            etCorreo.setText(p.correo)
                            etFecha.setText(p.fechanac)
                            etFoto.setText(p.foto)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }, { error ->
            runOnUiThread {
                Toast.makeText(this, "Error leyendo IDs: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
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
            showAlert("Validación", "Nombres y apellidos son obligatorios")
            return
        }
        helper.create(p) { ok, err ->
            runOnUiThread {
                if (ok) {
                    // Mostrar confirmación con el ID generado
                    showAlert("Registro exitoso", "Persona guardada con ID: ${p.id}")
                    etId.setText(p.id)
                } else {
                    showAlert("Error", err ?: "Error desconocido")
                }
            }
        }
    }

    private fun readPerson() {
        val id = etId.text.toString().trim()
        if (id.isBlank()) {
            showAlert("Información", "Proporciona un ID para buscar")
            return
        }
        val ref = com.google.firebase.database.FirebaseDatabase.getInstance().reference.child("personas").child(id)
        ref.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val p = snapshot.getValue(Personas::class.java)
                if (p != null) {
                    etNombres.setText(p.nombres)
                    etApellidos.setText(p.apellidos)
                    etCorreo.setText(p.correo)
                    etFecha.setText(p.fechanac)
                    etFoto.setText(p.foto)
                } else {
                    showAlert("Info", "No se encontró la persona con ID: $id")
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                showAlert("Error", error.message)
            }
        })
    }

    private fun updatePerson() {
        val id = etId.text.toString().trim()
        if (id.isBlank()) {
            showAlert("Información", "Proporciona un ID para actualizar")
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
        // Confirmación antes de actualizar
        AlertDialog.Builder(this)
            .setTitle("Confirmar actualización")
            .setMessage("¿Está seguro de que desea modificar los datos de este usuario?")
            .setPositiveButton("Sí") { _, _ ->
                helper.update(id, p) { ok, err ->
                    runOnUiThread {
                        if (ok) showAlert("Actualizado", "Los datos fueron actualizados") else showAlert("Error", err ?: "Error desconocido")
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deletePerson() {
        val id = etId.text.toString().trim()
        if (id.isBlank()) {
            showAlert("Información", "Proporciona un ID para borrar")
            return
        }
        // Confirmación antes de eliminar
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro de querer borrar este usuario?")
            .setPositiveButton("Sí") { _, _ ->
                helper.delete(id) { ok, err ->
                    runOnUiThread {
                        if (ok) {
                            showAlert("Eliminado", "La persona fue eliminada")
                            etId.setText("")
                            etNombres.setText("")
                            etApellidos.setText("")
                            etCorreo.setText("")
                            etFecha.setText("")
                            etFoto.setText("")
                        } else showAlert("Error", err ?: "Error desconocido")
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}

