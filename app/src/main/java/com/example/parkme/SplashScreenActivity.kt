package com.example.parkme

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.parkme.activitys.LoginActivity
import com.example.parkme.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    companion object {
        private const val SPLASH_TIME_OUT:Long = 1500 // 1,5 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = firebaseAuth.currentUser?.email
        Log.e("user", user.toString())
        Handler(Looper.getMainLooper()).postDelayed({
            if (user == null) {
                // User is already authenticated, redirect to the main activity
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // User is not authenticated, redirect to the login activity
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, SPLASH_TIME_OUT)
    }
}