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
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialog

class ConfiguracionFragment : Fragment() {

    var cambiofoto: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root: View = inflater.inflate(R.layout.fragment_configuracion, container, false)

        cambiofoto = root.findViewById(R.id.btneditarfoto);

        val cambiocontra =
            Navigation.createNavigateOnClickListener(R.id.action_nav_configuracion_to_cambiocontraFragment)
        root.btnCambiocontra.setOnClickListener {
            cambiocontra.onClick(it)
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
            imageView16.setImageBitmap(imageBitmap)
            println("ESTA ES LA IMAGEN  " + imageBitmap)
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
            imageView16.setImageURI(data)
        }
    }

}