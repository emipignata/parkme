package com.example.parkme.activitys

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.parkme.MainActivity
import com.example.parkme.R
import com.example.parkme.databinding.ActivityLoginBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Message
import com.example.parkme.entities.User
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private lateinit var button : Button
    companion object {
        private const val RC_SIGN_IN = 123
        private val firebaseAuth = FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        button = findViewById(R.id.btnSignIn)
        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(true)
            .build()

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val userAuth = GoogleAuthProvider.getCredential(credential?.googleIdToken, null)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        val email = credential.id
                        Toast.makeText(applicationContext, "token: $email", Toast.LENGTH_SHORT).show()
                        firebaseAuth.signInWithCredential(userAuth)
                            .addOnCompleteListener(this) { task ->
                                Log.e("TAG", "signInWithCredential:onComplete:" + task.getResult().toString())
                                if (task.isSuccessful) {
                                    // Sign-in success, update UI with the signed-in user's information
                                    checkAndCreateUserInFirestore()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(applicationContext, task.getResult().toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        button.setOnClickListener(
            View.OnClickListener { oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        activityResultLauncher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("TAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No Google Accounts found. Just continue presenting the signed-out UI.
                    showNoGoogleAccountInDeviceWarning()
                    Log.d("TAG", e.localizedMessage)
                } })
    }

    private fun checkAndCreateUserInFirestore() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userRef = db.collection("users").document(uid)

            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        // User exists in Firestore, no need to create it
                    } else if (currentUser != null){
                        // User doesn't exist, create a new document
                        val user = currentUser.displayName?.let { currentUser.email?.let { it1 -> User(userId = uid, nombre = it,email = it1, urlImage = currentUser.photoUrl.toString()) } }
                        if (user != null) {
                            userRef.set(user)
                        }
                    }
                } else {
                    // Handle the error
                    val exception = task.exception
                    if (exception != null) {
                        // Handle the error
                    }
                }
            }
        }
    }

    private fun showNoGoogleAccountInDeviceWarning() {
        AlertDialog.Builder(this)
            .setTitle("No registrado con Google")
            .setMessage("Esta aplicacion requiere estar concectado a una cuenta de google. Por favor inicie sesion en google")
            .setNegativeButton("Exit") { dialog, _ ->
                dialog.dismiss()
                finish() // Close the app
            }
            .setPositiveButton("Open Settings") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(android.provider.Settings.ACTION_ADD_ACCOUNT)
                intent.putExtra(android.provider.Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                startActivity(intent)
                finish()
            }
            .setCancelable(false) // Prevent users from dismissing the dialog
            .show()
    }

}