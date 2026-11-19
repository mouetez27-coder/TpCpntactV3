package com.example.tpcontacts.ui.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.tpcontacts.MainActivity
import com.example.tpcontacts.R
import com.example.tpcontacts.model.Contact
import com.example.tpcontacts.ui.ContactsViewModel
import com.example.tpcontacts.util.CoordinateError
import com.example.tpcontacts.util.CoordinateValidator
import java.io.File
import java.io.FileOutputStream

/**
 * Fiche d'un contact : consultation, modification, photo, actions téléphone/e-mail
 * et accès à la carte. Le contact est identifié par son identifiant (Bundle).
 */
class DetailContactFragment : Fragment(R.layout.fragment_detail_contact) {

    private val viewModel: ContactsViewModel by activityViewModels()

    private lateinit var imgPhoto: ImageView
    private lateinit var edtNom: EditText
    private lateinit var edtPrenom: EditText
    private lateinit var edtTelephone: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtLatitude: EditText
    private lateinit var edtLongitude: EditText

    private var contactId: String = ""
    private var currentPhotoUri: String? = null
    private var initialPhotoResId: Int = R.drawable.person1

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        currentPhotoUri = uri.toString()
        displayPhoto()
    }

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap == null) return@registerForActivityResult
        val uri = saveBitmapToCache(bitmap) ?: return@registerForActivityResult
        currentPhotoUri = uri.toString()
        displayPhoto()
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), R.string.permission_camera_refusee, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactId = requireArguments().getString(ARG_CONTACT_ID).orEmpty()
        currentPhotoUri = savedInstanceState?.getString(KEY_PHOTO_URI)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgPhoto = view.findViewById(R.id.imgPhoto)
        edtNom = view.findViewById(R.id.edtNom)
        edtPrenom = view.findViewById(R.id.edtPrenom)
        edtTelephone = view.findViewById(R.id.edtTelephone)
        edtEmail = view.findViewById(R.id.edtEmail)
        edtLatitude = view.findViewById(R.id.edtLatitude)
        edtLongitude = view.findViewById(R.id.edtLongitude)

        val contact = viewModel.findById(contactId)
        if (contact == null) {
            Toast.makeText(requireContext(), R.string.contact_introuvable, Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        initialPhotoResId = contact.photoResId.takeIf { it != 0 } ?: R.drawable.person1
        if (currentPhotoUri == null) {
            currentPhotoUri = contact.photoUri
        }
        fillFields(contact)
        setupListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_PHOTO_URI, currentPhotoUri)
        super.onSaveInstanceState(outState)
    }

    private fun fillFields(contact: Contact) {
        edtNom.setText(contact.nom)
        edtPrenom.setText(contact.prenom)
        edtTelephone.setText(contact.telephone)
        edtEmail.setText(contact.email)
        edtLatitude.setText(contact.latitude?.toString().orEmpty())
        edtLongitude.setText(contact.longitude?.toString().orEmpty())
        displayPhoto()
    }

    private fun displayPhoto() {
        val uri = currentPhotoUri
        if (uri.isNullOrBlank()) {
            imgPhoto.setImageResource(initialPhotoResId)
            return
        }

        try {
            imgPhoto.setImageURI(Uri.parse(uri))
            if (imgPhoto.drawable == null) {
                imgPhoto.setImageResource(initialPhotoResId)
            }
        } catch (_: Exception) {
            imgPhoto.setImageResource(initialPhotoResId)
        }
    }

    private fun setupListeners() {
        requireView().findViewById<Button>(R.id.btnRetourDetail).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        requireView().findViewById<Button>(R.id.btnAnnuler).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        requireView().findViewById<Button>(R.id.btnEnregistrer).setOnClickListener {
            saveContact()
        }
        requireView().findViewById<Button>(R.id.btnGalerie).setOnClickListener {
            launchGallery()
        }
        requireView().findViewById<Button>(R.id.btnCamera).setOnClickListener {
            launchCameraWithPermission()
        }
        requireView().findViewById<Button>(R.id.btnCall).setOnClickListener {
            dialCurrentNumber()
        }
        requireView().findViewById<Button>(R.id.btnMail).setOnClickListener {
            sendEmailToCurrentAddress()
        }
        requireView().findViewById<Button>(R.id.btnVoirCarte).setOnClickListener {
            openMap()
        }
    }

    private fun launchGallery() {
        try {
            pickImageLauncher.launch("image/*")
        } catch (_: Exception) {
            Toast.makeText(requireContext(), R.string.photo_non_chargee, Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchCameraWithPermission() {
        val granted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        try {
            takePhotoLauncher.launch(null)
        } catch (_: Exception) {
            Toast.makeText(requireContext(), R.string.impossible_camera, Toast.LENGTH_SHORT).show()
        }
    }

    private fun dialCurrentNumber() {
        val telephone = edtTelephone.text.toString().trim()
        if (telephone.isEmpty()) {
            edtTelephone.error = getString(R.string.erreur_telephone)
            return
        }

        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telephone"))
        try {
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(requireContext(), R.string.impossible_appel, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmailToCurrentAddress() {
        val email = edtEmail.text.toString().trim()
        if (email.isEmpty()) {
            edtEmail.error = getString(R.string.erreur_email)
            return
        }

        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
        try {
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(requireContext(), R.string.aucune_application_email, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openMap() {
        val contact = viewModel.findById(contactId)
        if (contact?.hasValidCoordinates() == true) {
            (requireActivity() as MainActivity).openMap(contact.id)
        } else {
            Toast.makeText(requireContext(), R.string.carte_absente, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveContact() {
        val contact = viewModel.findById(contactId)
        if (contact == null) {
            Toast.makeText(requireContext(), R.string.contact_introuvable, Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        var valid = true

        if (edtNom.text.isBlank()) {
            edtNom.error = getString(R.string.erreur_nom)
            valid = false
        }
        if (edtPrenom.text.isBlank()) {
            edtPrenom.error = getString(R.string.erreur_prenom)
            valid = false
        }
        if (edtTelephone.text.isBlank()) {
            edtTelephone.error = getString(R.string.erreur_telephone)
            valid = false
        }
        if (edtEmail.text.isBlank()) {
            edtEmail.error = getString(R.string.erreur_email)
            valid = false
        }

        val coordinates = CoordinateValidator.validatePair(
            edtLatitude.text.toString(),
            edtLongitude.text.toString()
        )
        coordinates.latitudeError?.let {
            edtLatitude.error = coordinateErrorMessage(it, isLatitude = true)
            valid = false
        }
        coordinates.longitudeError?.let {
            edtLongitude.error = coordinateErrorMessage(it, isLatitude = false)
            valid = false
        }

        if (!valid) return

        val updated = contact.copy(
            nom = edtNom.text.toString().trim(),
            prenom = edtPrenom.text.toString().trim(),
            telephone = edtTelephone.text.toString().trim(),
            email = edtEmail.text.toString().trim(),
            photoUri = currentPhotoUri,
            photoResId = if (currentPhotoUri == null) contact.photoResId else 0,
            latitude = coordinates.latitude,
            longitude = coordinates.longitude
        )

        viewModel.update(updated)
        Toast.makeText(requireContext(), R.string.contact_enregistre, Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    private fun coordinateErrorMessage(error: CoordinateError, isLatitude: Boolean): String {
        return when (error) {
            CoordinateError.NON_NUMERIQUE -> getString(
                if (isLatitude) R.string.latitude_non_numerique else R.string.longitude_non_numerique
            )

            CoordinateError.HORS_LIMITES -> getString(
                if (isLatitude) R.string.latitude_hors_limites else R.string.longitude_hors_limites
            )

            CoordinateError.MANQUANTE -> getString(R.string.coordonnees_incompletes)
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri? {
        return try {
            val directory = File(requireContext().cacheDir, "photos").apply { mkdirs() }
            val file = File.createTempFile("contact_", ".jpg", directory)
            FileOutputStream(file).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
            }
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
        } catch (_: Exception) {
            Toast.makeText(requireContext(), R.string.photo_non_enregistree, Toast.LENGTH_SHORT).show()
            null
        }
    }

    companion object {
        private const val ARG_CONTACT_ID = "contactId"
        private const val KEY_PHOTO_URI = "photoUri"

        fun newInstance(contactId: String): DetailContactFragment {
            return DetailContactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CONTACT_ID, contactId)
                }
            }
        }
    }
}
