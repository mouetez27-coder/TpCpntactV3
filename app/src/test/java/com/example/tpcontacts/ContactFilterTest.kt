package com.example.tpcontacts

import com.example.tpcontacts.model.Contact
import com.example.tpcontacts.util.ContactFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactFilterTest {

    private val contacts = listOf(
        Contact(
            id = "1",
            nom = "Dupont",
            prenom = "Claire",
            telephone = "0612345678",
            email = "claire.dupont@example.com",
            latitude = 48.8566,
            longitude = 2.3522
        ),
        Contact(
            id = "2",
            nom = "Martin",
            prenom = "Thomas",
            telephone = "0711223344",
            email = "thomas.martin@example.com"
        ),
        Contact(
            id = "3",
            nom = "Bernard",
            prenom = "Sophie",
            telephone = "0655667788",
            email = "sophie.bernard@example.com"
        )
    )

    @Test
    fun rechercheParNomIgnoreLaCasse() {
        val resultat = ContactFilter.filter(contacts, "DUP")
        assertEquals(listOf("1"), resultat.map { it.id })
    }

    @Test
    fun rechercheParPrenomIgnoreLaCasse() {
        val resultat = ContactFilter.filter(contacts, "thOmAs")
        assertEquals(listOf("2"), resultat.map { it.id })
    }

    @Test
    fun rechercheParTelephoneAvecEspaces() {
        val resultat = ContactFilter.filter(contacts, "06 55 66 77 88")
        assertEquals(listOf("3"), resultat.map { it.id })
    }

    @Test
    fun recherchePartielleSurLeTelephone() {
        val resultat = ContactFilter.filter(contacts, "1122")
        assertEquals(listOf("2"), resultat.map { it.id })
    }

    @Test
    fun requeteVideOuEspacesRetourneTousLesContacts() {
        assertEquals(contacts, ContactFilter.filter(contacts, ""))
        assertEquals(contacts, ContactFilter.filter(contacts, "   "))
    }

    @Test
    fun requeteSansResultatRetourneUneListeVide() {
        assertTrue(ContactFilter.filter(contacts, "zzz").isEmpty())
    }

    @Test
    fun contactFiltreConserveSonIdentifiantEtSesDonnees() {
        val filtered = ContactFilter.filter(contacts, "bernard")

        assertEquals(1, filtered.size)
        assertEquals("3", filtered.single().id)
        assertEquals("Sophie", filtered.single().prenom)
    }
}
