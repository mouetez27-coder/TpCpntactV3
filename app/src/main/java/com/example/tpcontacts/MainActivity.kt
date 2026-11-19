package com.example.tpcontacts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tpcontacts.ui.detail.DetailContactFragment
import com.example.tpcontacts.ui.list.ListeContactsFragment
import com.example.tpcontacts.ui.map.CarteContactFragment

/**
 * Unique Activity de l'application : elle héberge tous les fragments dans un
 * FragmentContainerView. La navigation est dynamique (replace + addToBackStack).
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Au changement de configuration, le FragmentManager restaure déjà les
        // fragments affichés : on n'ajoute la liste que sur une création réelle.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ListeContactsFragment(), TAG_LISTE)
                .commit()
        }
    }

    fun openDetail(contactId: String) {
        if (supportFragmentManager.isStateSaved) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, DetailContactFragment.newInstance(contactId))
            .addToBackStack(TAG_DETAIL)
            .commit()
    }

    fun openMap(contactId: String) {
        if (supportFragmentManager.isStateSaved) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CarteContactFragment.newInstance(contactId))
            .addToBackStack(TAG_CARTE)
            .commit()
    }

    companion object {
        private const val TAG_LISTE = "liste_contacts"
        private const val TAG_DETAIL = "detail_contact"
        private const val TAG_CARTE = "carte_contact"
    }
}
