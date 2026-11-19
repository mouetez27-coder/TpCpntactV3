package com.example.tpcontacts

import com.example.tpcontacts.util.CoordinateError
import com.example.tpcontacts.util.CoordinateValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CoordinateValidatorTest {

    private val tolerance = 0.000001

    @Test
    fun latitudeValideEstAcceptee() {
        val resultat = CoordinateValidator.validateLatitude("48.8566")

        assertEquals(48.8566, resultat.value!!, tolerance)
        assertNull(resultat.error)
        assertTrue(resultat.isValid)
    }

    @Test
    fun latitudeAvecVirguleEstAcceptee() {
        val resultat = CoordinateValidator.validateLatitude("48,8566")

        assertEquals(48.8566, resultat.value!!, tolerance)
        assertTrue(resultat.isValid)
    }

    @Test
    fun latitudeAuxLimitesEstAcceptee() {
        assertTrue(CoordinateValidator.validateLatitude("-90").isValid)
        assertTrue(CoordinateValidator.validateLatitude("90").isValid)
    }

    @Test
    fun latitudeHorsLimitesEstRefusee() {
        assertEquals(CoordinateError.HORS_LIMITES, CoordinateValidator.validateLatitude("90.1").error)
        assertEquals(CoordinateError.HORS_LIMITES, CoordinateValidator.validateLatitude("-91").error)
    }

    @Test
    fun latitudeNonNumeriqueEstRefusee() {
        assertEquals(CoordinateError.NON_NUMERIQUE, CoordinateValidator.validateLatitude("abc").error)
    }

    @Test
    fun longitudeAuxLimitesEstAcceptee() {
        assertTrue(CoordinateValidator.validateLongitude("-180").isValid)
        assertTrue(CoordinateValidator.validateLongitude("180").isValid)
    }

    @Test
    fun longitudeHorsLimitesEstRefusee() {
        assertEquals(CoordinateError.HORS_LIMITES, CoordinateValidator.validateLongitude("181").error)
        assertEquals(CoordinateError.HORS_LIMITES, CoordinateValidator.validateLongitude("-180.5").error)
    }

    @Test
    fun paireVideEstValide() {
        val resultat = CoordinateValidator.validatePair("", "   ")

        assertNull(resultat.latitude)
        assertNull(resultat.longitude)
        assertNull(resultat.latitudeError)
        assertNull(resultat.longitudeError)
        assertTrue(resultat.isValid)
    }

    @Test
    fun paireCompleteValideEstAcceptee() {
        val resultat = CoordinateValidator.validatePair("48,8566", "2.3522")

        assertEquals(48.8566, resultat.latitude!!, tolerance)
        assertEquals(2.3522, resultat.longitude!!, tolerance)
        assertTrue(resultat.isValid)
    }

    @Test
    fun latitudeSansLongitudeEstRefusee() {
        val resultat = CoordinateValidator.validatePair("48.8566", "")

        assertFalse(resultat.isValid)
        assertEquals(CoordinateError.MANQUANTE, resultat.longitudeError)
    }

    @Test
    fun longitudeSansLatitudeEstRefusee() {
        val resultat = CoordinateValidator.validatePair("", "2.3522")

        assertFalse(resultat.isValid)
        assertEquals(CoordinateError.MANQUANTE, resultat.latitudeError)
    }

    @Test
    fun coordonneeHorsLimitesDansLaPaireEstSignalee() {
        val resultat = CoordinateValidator.validatePair("91", "200")

        assertEquals(CoordinateError.HORS_LIMITES, resultat.latitudeError)
        assertEquals(CoordinateError.HORS_LIMITES, resultat.longitudeError)
    }
}
