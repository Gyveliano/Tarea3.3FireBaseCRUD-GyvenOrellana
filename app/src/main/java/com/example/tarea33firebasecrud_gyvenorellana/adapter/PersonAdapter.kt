package com.example.tarea33firebasecrud_gyvenorellana.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarea33firebasecrud_gyvenorellana.R
import com.example.tarea33firebasecrud_gyvenorellana.model.Personas
import com.bumptech.glide.Glide
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

class PersonAdapter(
    private var items: MutableList<Personas> = mutableListOf(),
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onEdit(persona: Personas)
        fun onDelete(persona: Personas)
    }

    fun setItems(list: List<Personas>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = items[position]
        holder.bind(p)
        holder.itemView.findViewById<View>(R.id.btnEdit).setOnClickListener {
            val ctx = holder.itemView.context
            val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("ID", p.id)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(ctx, "ID copiado al portapapeles: ${p.id}", Toast.LENGTH_SHORT).show()
            listener.onEdit(p)
        }
        holder.itemView.findViewById<View>(R.id.btnDelete).setOnClickListener { listener.onDelete(p) }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tvName)
        private val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        private val img: ImageView = view.findViewById(R.id.imgProfile)

        fun bind(p: Personas) {
            tvName.text = "${p.nombres} ${p.apellidos}"
            tvEmail.text = p.correo
            if (p.foto.isNotBlank()) {
                Glide.with(img.context)
                    .load(p.foto)
                    .placeholder(R.drawable.ic_default_user)
                    .error(R.drawable.ic_default_user)
                    .centerCrop()
                    .into(img)
            } else {
                img.setImageResource(R.drawable.ic_default_user)
            }
        }
    }
}


