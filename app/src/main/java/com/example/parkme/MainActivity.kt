package com.example.parkme

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.parkme.databinding.ActivityMainBinding
import com.example.parkme.entities.User
import com.example.parkme.navigation.ExploreFrArgs
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        bottomNavView = binding.bottomNavigation

        val userJson = intent.getStringExtra("userJson")
        val user = if (userJson != null) {
            Gson().fromJson(userJson, User::class.java)
        } else {
            // Handle the case where "userJson" extra is not found or is null
            // You may want to provide a default user or take appropriate action.
            User() // This creates a default user if needed
        }

        initializeNavigation(user)
        NavigationUI.setupWithNavController(bottomNavView, navHostFragment.navController)
    }
    private fun initializeNavigation(user: User) {
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavView, navController)

        val exploreFragmentArgs = ExploreFrArgs(user).toBundle() // Create a bundle with the JSON string
        navController.navigate(R.id.exploreFr, exploreFragmentArgs)
    }
}
