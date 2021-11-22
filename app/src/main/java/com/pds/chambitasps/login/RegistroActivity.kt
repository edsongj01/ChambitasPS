package com.pds.chambitasps.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pds.chambitasps.R
import kotlinx.android.synthetic.main.activity_registro.*
import kotlinx.android.synthetic.main.activity_registro.btneditarfoto
import kotlinx.android.synthetic.main.activity_registro.imageView16
import kotlinx.android.synthetic.main.fragment_configuracion.*

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        btnBack.setOnClickListener {
            super.onBackPressed()
        }

        btneditarfoto.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme
            )
            val bottomSheetView: View = LayoutInflater.from(this)
                .inflate(
                    R.layout.dialog_foto,
                    findViewById<View>(R.id.contenedor_cambiofoto) as LinearLayout?
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

    }


    //CAMARA

    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {

                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(
                        intent,
                        REQUEST_IMAGE_CAPTURE
                    )
                }

                else -> requestPermissionLauncher1.launch(Manifest.permission.CAMERA)
            }
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(
                intent,
                REQUEST_IMAGE_CAPTURE
            )
        }

//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(
//            intent,
//            REQUEST_IMAGE_CAPTURE
//        )
    }

    private val requestPermissionLauncher1 = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        if (isGranted) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(
                intent,
                REQUEST_IMAGE_CAPTURE
            )
        } else {
            Toast.makeText(
                this,
                "Permission denied",
                Toast.LENGTH_SHORT
            ).show()
        }
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
                    this,
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
                this,
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