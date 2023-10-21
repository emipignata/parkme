package com.example.parkme.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel : ViewModel() {

    private val currentUserLiveData = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = currentUserLiveData

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var userJson: String? = null

    init {
        // Load user data when the ViewModel is created
        auth.currentUser?.let { currentUserLiveData.value = it } // Set the current user
        auth.addAuthStateListener { auth ->
            currentUserLiveData.value = auth.currentUser
        }
    }
}
