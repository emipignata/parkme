package com.example.parkme.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.parkme.R
import com.example.parkme.SplashScreenActivity
import com.example.parkme.databinding.FragmentPerfilBinding
import com.bumptech.glide.Glide

class PerfilFr : Fragment() {
    private lateinit var binding: FragmentPerfilBinding
    private val mGoogleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(requireContext(), gso)
    }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val currentUser: FirebaseUser by lazy { firebaseAuth.currentUser!! }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val view = binding.root
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        binding.profileEmail.text = currentUser.email
        binding.profileName.text = currentUser.displayName
       // binding.profileLastname.text = currentUser.displayName

      /*  Glide.with(binding.root.context)
            .load(R.drawable.circular_overlay) // Load the circular mask drawable
            .into(view.findViewById<ImageView>(R.id.profile_image))
            ACA TRATAMOS DE REDONDEAR LA FOTO PERO NO PUDIMOS....
        */
        Glide
            .with(binding.root.context)
            .load(currentUser.photoUrl)
            .into(binding.profileImage)



        btnLogout.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                firebaseAuth.signOut()
                val intent = Intent(requireContext(), SplashScreenActivity::class.java)
                Toast.makeText(requireContext(), mGoogleSignInClient.toString(), Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
        }
        return view
    }
}
