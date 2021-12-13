package com.pds.chambitasps.login

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pds.chambitasps.MenuActivity
import com.pds.chambitasps.R
import com.pds.chambitasps.util.Constants.Companion.ACTION_START_LOCATION_SERVICE
import com.pds.chambitasps.util.ForegroundLocationService
import kotlinx.android.synthetic.main.activity_registro.*
import kotlinx.android.synthetic.main.activity_registro.btneditarfoto
import kotlinx.android.synthetic.main.activity_registro.imageView16
import kotlinx.android.synthetic.main.fragment_configuracion.*

class RegistroActivity : AppCompatActivity() {

    var db: FirebaseFirestore = Firebase.firestore
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = Firebase.auth
        initUI()



    }

    private fun initUI() {
        btnBack.setOnClickListener {
            super.onBackPressed()
        }

        btnSiguienteRegistro.setOnClickListener {
            if (etxtIngresaTelefono.text.isEmpty() ||
                etxtIngresaNombre.text.isEmpty()) {
                Toast.makeText(this, "Completar todos los campos requeridos", Toast.LENGTH_SHORT).show()
            } else {
                registerUser()
            }
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

    private fun registerUser() {
        val phone = etxtIngresaTelefono.text.toString()
        val name = etxtIngresaNombre.text.toString()
        val type = "prestador"
        val car = "Sedan"
        val photo = ""

        val user = auth.currentUser
        user?.let {
            val uid = user.uid
            val email = user.email
            val data = hashMapOf(
                "email" to email,
                "name" to name,
                "phone" to phone,
                "car" to car,
                "type" to type
            )
            db.collection("usuarios").document(uid).set(data)
                .addOnCompleteListener {
                    Log.d("RegistroUser", "Perfil del usuario ingresado correctamente")
                }
                .addOnFailureListener { e ->
                    Log.e("RegistroUser", "Error al registrar la informacion del usuario", e)
                }
            startActivity(Intent(this, MenuActivity::class.java))
            startLocationService()
            finish()
        }
    }

    private fun isLocationServiceRunning(): Boolean {
        val activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (activityManager != null) {
            for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (ForegroundLocationService::class.java.name.equals(service.service.className)) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }
        return false
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()){
            val intent = Intent(applicationContext, ForegroundLocationService::class.java)
            intent.action = ACTION_START_LOCATION_SERVICE
            startService(intent)
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