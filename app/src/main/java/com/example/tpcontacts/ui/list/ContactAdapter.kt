package com.example.tpcontacts.ui.list

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tpcontacts.R
import com.example.tpcontacts.model.Contact

/**
 * Adapter RecyclerView : chaque ligne affiche la photo, le nom, le prénom et le
 * téléphone. Le contact cliqué est transmis par identifiant, jamais par position.
 */
class ContactAdapter(
    private val onContactClick: (Contact) -> Unit
) : ListAdapter<Contact, ContactAdapter.ContactViewHolder>(DIFF_CALLBACK) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view, onContactClick)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ContactViewHolder(
        itemView: View,
        private val onContactClick: (Contact) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val txtNom: TextView = itemView.findViewById(R.id.txtNom)
        private val txtPrenom: TextView = itemView.findViewById(R.id.txtPrenom)
        private val txtTelephone: TextView = itemView.findViewById(R.id.txtTel)
        private val imgPhoto: ImageView = itemView.findViewById(R.id.imgPhoto)

        fun bind(contact: Contact) {
            txtNom.text = contact.nom
            txtPrenom.text = contact.prenom
            txtTelephone.text = contact.telephone
            bindPhoto(contact)

            itemView.setOnClickListener { onContactClick(contact) }
        }

        private fun bindPhoto(contact: Contact) {
            val fallback = contact.photoResId.takeIf { it != 0 } ?: R.drawable.person1
            imgPhoto.setImageResource(fallback)

            val uri = contact.photoUri ?: return
            try {
                imgPhoto.setImageURI(Uri.parse(uri))
            } catch (_: Exception) {
                imgPhoto.setImageResource(fallback)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Contact>() {
            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean =
                oldItem == newItem
        }
    }
}
