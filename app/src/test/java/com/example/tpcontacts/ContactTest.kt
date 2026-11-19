package com.example.tpcontacts

import com.example.tpcontacts.model.Contact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactTest {

    private fun contact(latitude: Double?, longitude: Double?) = Contact(
        id = "1",
        nom = "Dupont",
        prenom = "Claire",
        telephone = "0612345678",
        email = "claire.dupont@example.com",
        latitude = latitude,
        longitude = longitude
    )

    @Test
    fun nomCompletEstFormate() {
        assertEquals("Claire Dupont", contact(null, null).nomComplet)
    }

    @Test
    fun coordonneesAbsentesNeSontPasValides() {
        assertFalse(contact(null, null).hasValidCoordinates())
    }

    @Test
    fun coordonneesDansLesBornesSontValides() {
        assertTrue(contact(48.8566, 2.3522).hasValidCoordinates())
        assertTrue(contact(-90.0, -180.0).hasValidCoordinates())
        assertTrue(contact(90.0, 180.0).hasValidCoordinates())
    }

    @Test
    fun coordonneesHorsBornesNeSontPasValides() {
        assertFalse(contact(91.0, 2.3522).hasValidCoordinates())
        assertFalse(contact(48.8566, -181.0).hasValidCoordinates())
    }
}
