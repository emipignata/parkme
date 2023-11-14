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
import com.example.parkme.entities.User
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private lateinit var button: Button

    companion object {
        private val firebaseAuth = FirebaseAuth.getInstance()
        private const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        button = findViewById(R.id.btnSignIn)
        oneTapClient = Identity.getSignInClient(this)
        initializeSignInRequest()

        button.setOnClickListener(View.OnClickListener {
            showGoogleSignInDialog()
        })
    }

    private fun initializeSignInRequest() {
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun showGoogleSignInDialog() {
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    startIntentSenderForResult(
                        intentSenderRequest.intentSender,
                        RC_SIGN_IN,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("TAG", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                showNoGoogleAccountInDeviceWarning()
                Log.d("TAG", e.localizedMessage)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                handleSignInResult(data)
            }
        }
    }

    private fun handleSignInResult(data: Intent?) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val userAuth = GoogleAuthProvider.getCredential(credential?.googleIdToken, null)
            val idToken = credential?.googleIdToken
            if (idToken != null) {
                val email = credential.id
                firebaseAuth.signInWithCredential(userAuth)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            checkAndCreateUserInFirestore()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(applicationContext, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } catch (e: ApiException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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

                    } else if (currentUser != null) {

                        val user = currentUser.displayName?.let { currentUser.email?.let { it1 -> User(userId = uid, nombre = it, email = it1, urlImage = currentUser.photoUrl.toString()) } }
                        if (user != null) {
                            userRef.set(user)
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception != null) {
                    }
                }
            }
        }
    }

    private fun showNoGoogleAccountInDeviceWarning() {
        AlertDialog.Builder(this)
            .setTitle("No registrado con Google")
            .setMessage("Esta aplicación requiere estar conectado a una cuenta de Google. Por favor, inicie sesión en Google.")
            .setNegativeButton("Salir") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setPositiveButton("Abrir configuración") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(android.provider.Settings.ACTION_ADD_ACCOUNT)
                intent.putExtra(android.provider.Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                recreate()
            }
            .setCancelable(false)
            .show()
    }


}
