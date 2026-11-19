package com.example.tpcontacts.util

/** Erreur de validation d'une coordonnée. */
enum class CoordinateError {
    NON_NUMERIQUE,
    HORS_LIMITES,
    MANQUANTE
}

data class CoordinateResult(
    val value: Double?,
    val error: CoordinateError?
) {
    val isValid: Boolean get() = error == null
}

data class CoordinatesPairResult(
    val latitude: Double?,
    val longitude: Double?,
    val latitudeError: CoordinateError?,
    val longitudeError: CoordinateError?
) {
    val isValid: Boolean get() = latitudeError == null && longitudeError == null
}

/**
 * Validation des coordonnées GPS saisies manuellement.
 *
 * Une coordonnée vide est autorisée, mais les deux doivent alors être vides.
 * La virgule décimale est acceptée (clavier français).
 */
object CoordinateValidator {

    fun validateLatitude(raw: String): CoordinateResult {
        val text = normalize(raw)
        if (text.isEmpty()) return CoordinateResult(null, null)

        val value = text.toDoubleOrNull()
            ?: return CoordinateResult(null, CoordinateError.NON_NUMERIQUE)

        if (value < -90.0 || value > 90.0) {
            return CoordinateResult(null, CoordinateError.HORS_LIMITES)
        }
        return CoordinateResult(value, null)
    }

    fun validateLongitude(raw: String): CoordinateResult {
        val text = normalize(raw)
        if (text.isEmpty()) return CoordinateResult(null, null)

        val value = text.toDoubleOrNull()
            ?: return CoordinateResult(null, CoordinateError.NON_NUMERIQUE)

        if (value < -180.0 || value > 180.0) {
            return CoordinateResult(null, CoordinateError.HORS_LIMITES)
        }
        return CoordinateResult(value, null)
    }

    fun validatePair(latitudeRaw: String, longitudeRaw: String): CoordinatesPairResult {
        val latitude = validateLatitude(latitudeRaw)
        val longitude = validateLongitude(longitudeRaw)

        var latitudeError = latitude.error
        var longitudeError = longitude.error

        if (latitudeError == null && longitudeError == null) {
            if (latitude.value == null && longitude.value != null) {
                latitudeError = CoordinateError.MANQUANTE
            } else if (latitude.value != null && longitude.value == null) {
                longitudeError = CoordinateError.MANQUANTE
            }
        }

        return CoordinatesPairResult(
            latitude = latitude.value,
            longitude = longitude.value,
            latitudeError = latitudeError,
            longitudeError = longitudeError
        )
    }

    private fun normalize(raw: String): String = raw.trim().replace(',', '.')
}
