package com.pds.chambitasps.ui.configuracion

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.pds.chambitasps.R
import kotlinx.android.synthetic.main.fragment_aceptacionservicio.view.*
import kotlinx.android.synthetic.main.fragment_configuracion.*
import kotlinx.android.synthetic.main.fragment_configuracion.view.*
import androidx.core.app.ActivityCompat.startActivityForResult
import android.graphics.BitmapFactory

import android.R.attr.data
import android.util.Base64
import android.util.Log
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream

class ConfiguracionFragment : Fragment() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var profilePhoto: String = ""

    var cambiofoto: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root: View = inflater.inflate(R.layout.fragment_configuracion, container, false)

        val user = auth.currentUser
        db.collection("usuarios")
            .document(user!!.uid)
            .get()
            .addOnSuccessListener { userProfile ->
                if (userProfile != null && userProfile.exists()) {
                    if (userProfile.data!!["photo"] != null) {
                        val imageBytes = Base64.decode(userProfile.data!!["photo"].toString(), Base64.DEFAULT)
                        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        root.photoimage.setImageBitmap(decodedImage)
                    }
                    root.etxtIngresaUsuarioPer.setText(userProfile.data!!["name"].toString())
                    root.etxtIngresaTelefonoPer.setText(userProfile.data!!["phone"].toString())
                    root.etxtIngresaCarroPer.setText(userProfile.data!!["car"].toString())
                }
            }
            .addOnFailureListener {
                Log.d("Perfil", "No se pudo consultar el perfil")
            }

        cambiofoto = root.findViewById(R.id.btneditarfoto);
/*
        val cambiocontra =
            Navigation.createNavigateOnClickListener(R.id.action_nav_configuracion_to_cambiocontraFragment)
        root.btnCambiocontra.setOnClickListener {
            cambiocontra.onClick(it)
        }
 */

        root.btnPerfilCam.setOnClickListener {

            val changes = hashMapOf<String, Any>(
                "name" to root.etxtIngresaUsuarioPer.text.toString(),
                "phone" to root.etxtIngresaTelefonoPer.text.toString(),
                "car" to root.etxtIngresaCarroPer.text.toString(),
            )
            if (profilePhoto != "") {
                changes.put("photo", profilePhoto)
            }

            db.collection("usuarios")
                .document(user!!.uid)
                .update(changes)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Cambios guardados correctamente", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Hubo un problema al guardar los cambios", Toast.LENGTH_LONG).show()
                    Log.d("Perfil", "No se pudieron guardar los cambios", it)
                }
        }

        cambiofoto?.setOnClickListener {
            //requestPermission()
            //dispatchTakePictureIntent()

            val bottomSheetDialog = BottomSheetDialog(
                requireContext(), R.style.BottomSheetDialogTheme
            )
            val bottomSheetView: View = LayoutInflater.from(context)
                .inflate(
                    R.layout.dialog_foto,
                    root.findViewById<View>(R.id.contenedor_cambiofoto) as LinearLayout?
                )
            bottomSheetView.findViewById<View>(R.id.btnCamara).setOnClickListener {
                dispatchTakePictureIntent()
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<View>(R.id.btnAlbum).setOnClickListener {
                requestPermission()
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<View>(R.id.btnCancelar).setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }


        return root
    }

    //CAMARA

    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(
            intent,
            REQUEST_IMAGE_CAPTURE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            photoimage.setImageBitmap(imageBitmap)
            println("ESTA ES LA IMAGEN  " + imageBitmap)

            val baos: ByteArrayOutputStream = ByteArrayOutputStream()
            val bitmap = imageBitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imagesBytes = baos.toByteArray()
            val imageString = Base64.encodeToString(imagesBytes, Base64.DEFAULT)
            profilePhoto = imageString
            Log.d("Perfil", "Image 64: ${imageString}")
        }
    }

    // GALERIA
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {

                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickPhotoFromGallery()
                }

                else -> requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            pickPhotoFromGallery()
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityResult.launch(intent)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        if (isGranted) {
            pickPhotoFromGallery()
        } else {
            Toast.makeText(
                context,
                "Permission denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val startForActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            photoimage.setImageURI(data)
        }
    }

}