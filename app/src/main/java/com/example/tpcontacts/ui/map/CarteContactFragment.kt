package com.example.tpcontacts.ui.map

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.tpcontacts.R
import com.example.tpcontacts.ui.ContactsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Carte Google Maps centrée sur le contact, avec un marqueur à son nom.
 * Le SupportMapFragment est ajouté dynamiquement et réutilisé après rotation.
 */
class CarteContactFragment : Fragment(R.layout.fragment_carte_contact), OnMapReadyCallback {

    private val viewModel: ContactsViewModel by activityViewModels()

    private var contactId: String = ""
    private lateinit var mapContainer: FrameLayout
    private lateinit var txtInfos: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactId = requireArguments().getString(ARG_CONTACT_ID).orEmpty()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapContainer = view.findViewById(R.id.mapContainer)
        txtInfos = view.findViewById(R.id.txtInfosCarte)

        view.findViewById<Button>(R.id.btnRetourCarte).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val contact = viewModel.findById(contactId)
        val latitude = contact?.latitude
        val longitude = contact?.longitude

        if (contact == null || latitude == null || longitude == null || !contact.hasValidCoordinates()) {
            showMissingPosition()
            return
        }

        txtInfos.text = getString(R.string.carte_position, latitude, longitude)
        mapContainer.isVisible = true

        if (!hasMapsApiKey()) {
            mapContainer.isVisible = false
            txtInfos.text = getString(R.string.carte_position, latitude, longitude) +
                "\n\n" + getString(R.string.carte_indisponible)
            return
        }

        val existing = childFragmentManager.findFragmentById(R.id.mapContainer) as? SupportMapFragment
        val mapFragment = existing ?: SupportMapFragment.newInstance().also { fragment ->
            childFragmentManager.beginTransaction()
                .replace(R.id.mapContainer, fragment, TAG_MAP)
                .commit()
        }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val contact = viewModel.findById(contactId) ?: return
        val latitude = contact.latitude ?: return
        val longitude = contact.longitude ?: return

        val position = LatLng(latitude, longitude)
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM_CONTACT))
        googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .title(contact.nomComplet)
                .snippet(contact.telephone)
        )
    }

    private fun showMissingPosition() {
        mapContainer.isVisible = false
        txtInfos.isVisible = true
        txtInfos.text = getString(R.string.carte_absente)
    }

    /** Vérifie que la clé lue depuis local.properties a bien été injectée dans le manifest. */
    private fun hasMapsApiKey(): Boolean {
        return try {
            val applicationInfo = requireContext().packageManager.getApplicationInfo(
                requireContext().packageName,
                PackageManager.GET_META_DATA
            )
            !applicationInfo.metaData?.getString(MAPS_API_KEY_META_DATA).isNullOrBlank()
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        private const val ARG_CONTACT_ID = "contactId"
        private const val TAG_MAP = "carte_google_maps"
        private const val ZOOM_CONTACT = 14f
        private const val MAPS_API_KEY_META_DATA = "com.google.android.geo.API_KEY"

        fun newInstance(contactId: String): CarteContactFragment {
            return CarteContactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CONTACT_ID, contactId)
                }
            }
        }
    }
}
