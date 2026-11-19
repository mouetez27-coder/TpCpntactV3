package com.example.tpcontacts.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tpcontacts.data.ContactRepository
import com.example.tpcontacts.model.Contact

/**
 * ViewModel partagé entre tous les fragments de MainActivity (activityViewModels()).
 *
 * Choix de communication : le ViewModel partagé évite de faire transiter l'objet
 * Contact complet dans des Intents/Bundles. Seul l'identifiant du contact voyage
 * dans un Bundle, et la liste observée est automatiquement actualisée après
 * l'enregistrement depuis le détail.
 */
class ContactsViewModel : ViewModel() {

    private val _contacts = MutableLiveData(ContactRepository.all())
    val contacts: LiveData<List<Contact>> = _contacts

    fun findById(id: String): Contact? = ContactRepository.findById(id)

    fun update(contact: Contact) {
        if (ContactRepository.update(contact)) {
            _contacts.value = ContactRepository.all()
        }
    }
}
