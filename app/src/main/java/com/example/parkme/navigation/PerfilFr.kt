package com.example.parkme.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.parkme.R
import com.example.parkme.databinding.FragmentPerfilBinding
import com.bumptech.glide.Glide
import com.example.parkme.SplashScreenActivity
import com.example.parkme.entities.User
import com.google.firebase.firestore.FirebaseFirestore

class PerfilFr : Fragment() {
    private lateinit var binding: FragmentPerfilBinding
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid!!
    val userRef = db.collection("users").document(uid)
    private val mGoogleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(requireContext(), gso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateUser()
    }

    private fun populateUser() {
        userRef.get().addOnSuccessListener {
            if (it.exists()) {
                val user = it.toObject(User::class.java)
                binding.profileName.text = user?.nombre
                binding.profileEmail.text = user?.email
                Glide
                    .with(binding.root.context)
                    .load(user?.urlImage).circleCrop()
                    .into(binding.profileImage)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val view = binding.root
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)





        btnLogout.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireContext(), SplashScreenActivity::class.java)
                startActivity(intent)
            }
        }
        return view
    }
}