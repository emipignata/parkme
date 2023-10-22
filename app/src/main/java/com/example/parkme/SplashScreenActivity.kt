package com.example.parkme

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.parkme.activitys.LoginActivity
import com.example.parkme.databinding.ActivitySplashScreenBinding
import com.example.parkme.viewmodels.UserViewModel

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val user = firebaseAuth.currentUser?.uid

    companion object {
        private const val SPLASH_TIME_OUT:Long = 1500 // 1,5 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationPermissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[ACCESS_FINE_LOCATION] == true && permissions[ACCESS_COARSE_LOCATION] == true) {
            // Permissions granted, continue with the app

            Handler(Looper.getMainLooper()).postDelayed({
                if (user == null) {
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    mainActivityIntent.putExtra("user", user)
                    startActivity(mainActivityIntent)
                }
                finish()
            }, SPLASH_TIME_OUT)
        } else {
            // Permissions not granted, show the permission dialog
            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) ||
                shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)
            ) {
                showPermissionDeniedMessageAndRetry()
            } else {
                // Explain the importance of the permission and direct the user to the app settings
                showPermissionDeniedMessageAndOpenAppSettings()
            }
        }

    }
    private fun showPermissionDeniedMessageAndRetry() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("This app requires location access to function properly. Please grant the permission.")
            .setNegativeButton("Exit") { dialog, _ ->
                dialog.dismiss()
                finish() // Close the app
            }
            .setPositiveButton("Retry") { dialog, _ ->
                dialog.dismiss()
                locationPermissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
            }
            .setCancelable(false) // Prevent users from dismissing the dialog
            .show()
    }
    private fun showPermissionDeniedMessageAndOpenAppSettings() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("This app requires location access to function properly. Please grant the permission in your device settings.")
            .setNegativeButton("Exit") { dialog, _ ->
                dialog.dismiss()
                finish() // Close the app
            }
            .setPositiveButton("Open Settings") { dialog, _ ->
                dialog.dismiss()
                // Open app settings so the user can manually grant the permission
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                finish()
            }
            .setCancelable(false) // Prevent users from dismissing the dialog
            .show()
    }
}