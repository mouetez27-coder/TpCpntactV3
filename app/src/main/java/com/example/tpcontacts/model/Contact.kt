package com.example.tpcontacts.model

/**
 * Modèle immutable d'un contact.
 *
 * L'identifiant [id] est stable : il est transmis entre les fragments à la place
 * de la position dans la liste, ce qui évite toute erreur d'indice après filtrage.
 */
data class Contact(
    val id: String,
    val nom: String,
    val prenom: String,
    val telephone: String,
    val email: String,
    val photoResId: Int = 0,
    val photoUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    val nomComplet: String
        get() = "$prenom $nom".trim()

    fun hasValidCoordinates(): Boolean {
        val lat = latitude ?: return false
        val lon = longitude ?: return false
        return lat in -90.0..90.0 && lon in -180.0..180.0
    }
}
