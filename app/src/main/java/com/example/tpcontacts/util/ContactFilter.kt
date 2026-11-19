package com.example.tpcontacts.util

import com.example.tpcontacts.model.Contact
import java.util.Locale

/**
 * Filtrage pur (sans Android) de la liste des contacts.
 *
 * La recherche ignore la casse et les espaces du numéro de téléphone.
 * Elle renvoie une nouvelle liste : la liste source et les positions d'origine
 * ne sont jamais utilisées pour identifier un contact.
 */
object ContactFilter {

    fun filter(contacts: List<Contact>, query: String): List<Contact> {
        val search = query.trim().lowercase(Locale.ROOT)
        if (search.isEmpty()) return contacts

        val phoneSearch = search.filterNot { it.isWhitespace() }

        return contacts.filter { contact ->
            contact.nom.lowercase(Locale.ROOT).contains(search) ||
                contact.prenom.lowercase(Locale.ROOT).contains(search) ||
                contact.telephone.filterNot { it.isWhitespace() }.contains(phoneSearch)
        }
    }
}
