package com.example.tpcontacts.data

import com.example.tpcontacts.R
import com.example.tpcontacts.model.Contact

/**
 * Source de données en mémoire (aucune persistance demandée par l'énoncé).
 *
 * Le repository est un singleton applicatif : la liste survit donc aux rotations
 * d'écran et aux recréations de fragments, sans empiler de données en double.
 */
object ContactRepository {

    private val contacts = mutableListOf(
        Contact(
            id = "contact-1",
            nom = "Dupont",
            prenom = "Claire",
            telephone = "0612345678",
            email = "claire.dupont@example.com",
            photoResId = R.drawable.oip,
            latitude = 48.8566,
            longitude = 2.3522
        ),
        Contact(
            id = "contact-2",
            nom = "Martin",
            prenom = "Thomas",
            telephone = "0711223344",
            email = "thomas.martin@example.com",
            photoResId = R.drawable.oip2,
            latitude = 45.7640,
            longitude = 4.8357
        ),
        Contact(
            id = "contact-3",
            nom = "Bernard",
            prenom = "Sophie",
            telephone = "0655667788",
            email = "sophie.bernard@example.com",
            photoResId = R.drawable.oip3,
            latitude = null,
            longitude = null
        )
    )

    fun all(): List<Contact> = contacts.toList()

    fun findById(id: String): Contact? = contacts.firstOrNull { it.id == id }

    /** Remplace le contact portant le même identifiant. Retourne false s'il est inconnu. */
    fun update(contact: Contact): Boolean {
        val index = contacts.indexOfFirst { it.id == contact.id }
        if (index == -1) return false
        contacts[index] = contact
        return true
    }
}
