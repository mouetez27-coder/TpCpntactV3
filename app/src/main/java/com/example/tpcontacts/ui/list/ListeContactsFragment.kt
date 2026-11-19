package com.example.tpcontacts.ui.list

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tpcontacts.MainActivity
import com.example.tpcontacts.R
import com.example.tpcontacts.model.Contact
import com.example.tpcontacts.ui.ContactsViewModel
import com.example.tpcontacts.util.ContactFilter

/**
 * Liste des contacts : recherche en temps réel au-dessus d'une RecyclerView.
 */
class ListeContactsFragment : Fragment(R.layout.fragment_liste_contacts) {

    private val viewModel: ContactsViewModel by activityViewModels()

    private lateinit var adapter: ContactAdapter
    private lateinit var recyclerContacts: RecyclerView
    private lateinit var txtAucunContact: TextView
    private lateinit var edtRecherche: EditText

    private var contacts: List<Contact> = emptyList()
    private var query: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtRecherche = view.findViewById(R.id.edtRecherche)
        recyclerContacts = view.findViewById(R.id.recyclerContacts)
        txtAucunContact = view.findViewById(R.id.txtAucunContact)

        query = savedInstanceState?.getString(KEY_QUERY).orEmpty()

        adapter = ContactAdapter { contact ->
            (requireActivity() as MainActivity).openDetail(contact.id)
        }
        recyclerContacts.layoutManager = LinearLayoutManager(requireContext())
        recyclerContacts.adapter = adapter

        viewModel.contacts.observe(viewLifecycleOwner) { liste ->
            contacts = liste
            applyFilter()
        }

        edtRecherche.setText(query)
        edtRecherche.setSelection(edtRecherche.text.length)
        edtRecherche.addTextChangedListener { editable ->
            query = editable?.toString().orEmpty()
            applyFilter()
        }

        applyFilter()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // L'EditText restaure son texte automatiquement : on resynchronise le filtre.
        val texteRestaure = edtRecherche.text?.toString().orEmpty()
        if (texteRestaure != query) {
            query = texteRestaure
            applyFilter()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_QUERY, query)
        super.onSaveInstanceState(outState)
    }

    private fun applyFilter() {
        val filtered = ContactFilter.filter(contacts, query)
        adapter.submitList(filtered)
        txtAucunContact.isVisible = filtered.isEmpty()
        recyclerContacts.isVisible = filtered.isNotEmpty()
    }

    companion object {
        private const val KEY_QUERY = "query"
    }
}
