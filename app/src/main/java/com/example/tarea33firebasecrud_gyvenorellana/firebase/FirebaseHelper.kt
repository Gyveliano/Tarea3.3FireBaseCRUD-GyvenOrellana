package com.example.tarea33firebasecrud_gyvenorellana.firebase

import com.example.tarea33firebasecrud_gyvenorellana.model.Personas
import com.google.firebase.database.*
import android.util.Log

private const val TAG = "FirebaseHelper"

class FirebaseHelper {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("personas")

    fun create(persona: Personas, onComplete: (Boolean, String?) -> Unit) {
        // Generar un ID con formato YYYYMMDD + contador atómico en la rama counters/usuarios
        val countersRef = database.root.child("counters").child("usuarios")
        countersRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val current = when (val v = currentData.value) {
                    is Long -> v
                    is Int -> v.toLong()
                    else -> 0L
                }
                val next = current + 1L
                currentData.value = next
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (error != null) {
                    Log.e(TAG, "Error en transaccion contador: ${error.message}")
                    onComplete(false, error.message)
                    return
                }
                val counterVal = when (val v = snapshot?.value) {
                    is Long -> v
                    is Int -> v.toLong()
                    is String -> v.toLongOrNull() ?: 0L
                    else -> 0L
                }
                // fecha actual
                val cal = java.util.Calendar.getInstance()
                val year = cal.get(java.util.Calendar.YEAR)
                val month = cal.get(java.util.Calendar.MONTH) + 1
                val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
                val idStr = String.format("%04d%02d%02d%d", year, month, day, counterVal)
                persona.id = idStr
                Log.d(TAG, "Creando persona con ID custom: $idStr (contador: $counterVal)")
                database.child(idStr).setValue(persona).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Persona creada exitosamente: $idStr")
                    } else {
                        Log.e(TAG, "Error al crear persona: ${task.exception?.message}")
                    }
                    if (task.isSuccessful) onComplete(true, null) else onComplete(false, task.exception?.message)
                }
            }
        })
    }

    fun readAll(onData: (List<Personas>) -> Unit, onError: (DatabaseError) -> Unit) {
        Log.d(TAG, "Leyendo todas las personas")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange llamado, children count: ${snapshot.childrenCount}")
                val list = mutableListOf<Personas>()
                for (child in snapshot.children) {
                    val p = child.getValue(Personas::class.java)
                    p?.let { list.add(it) }
                }
                Log.d(TAG, "Personas leídas: ${list.size}")
                onData(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error al leer personas: ${error.message}")
                onError(error)
            }
        })
    }

    fun update(id: String, persona: Personas, onComplete: (Boolean, String?) -> Unit) {
        if (id.isBlank()) {
            Log.e(TAG, "Error: ID vacío para update")
            onComplete(false, "ID vacío")
            return
        }
        Log.d(TAG, "Actualizando persona con ID: $id")
        database.child(id).setValue(persona).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Persona actualizada exitosamente: $id")
            } else {
                Log.e(TAG, "Error al actualizar persona: ${task.exception?.message}")
            }
            if (task.isSuccessful) onComplete(true, null) else onComplete(false, task.exception?.message)
        }
    }

    fun delete(id: String, onComplete: (Boolean, String?) -> Unit) {
        if (id.isBlank()) {
            Log.e(TAG, "Error: ID vacío para delete")
            onComplete(false, "ID vacío")
            return
        }
        Log.d(TAG, "Eliminando persona con ID: $id")
        database.child(id).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Persona eliminada exitosamente: $id")
            } else {
                Log.e(TAG, "Error al eliminar persona: ${task.exception?.message}")
            }
            if (task.isSuccessful) onComplete(true, null) else onComplete(false, task.exception?.message)
        }
    }
}

