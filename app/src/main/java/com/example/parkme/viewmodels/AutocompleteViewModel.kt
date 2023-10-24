package com.example.parkme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AutocompleteViewModel : ViewModel() {

    private val _address1 = MutableLiveData<String>()
    val address1: LiveData<String> = _address1

    private val _address2 = MutableLiveData<String>()
    val address2: LiveData<String> = _address2

    private val _city = MutableLiveData<String>()
    val city: LiveData<String> = _city

    private val _state = MutableLiveData<String>()
    val state: LiveData<String> = _state

    private val _postal = MutableLiveData<String>()
    val postal: LiveData<String> = _postal

    private val _country = MutableLiveData<String>()
    val country: LiveData<String> = _country

    private val _proximityCheck = MutableLiveData<Boolean>()
    val proximityCheck: LiveData<Boolean> = _proximityCheck

    init {
        // Initialize default values if needed
        _proximityCheck.value = false
    }

    fun updateAddress1(value: String) {
        _address1.value = value
    }

    fun updateAddress2(value: String) {
        _address2.value = value
    }

    fun updateCity(value: String) {
        _city.value = value
    }

    fun updateState(value: String) {
        _state.value = value
    }

    fun updatePostal(value: String) {
        _postal.value = value
    }

    fun updateCountry(value: String) {
        _country.value = value
    }

    fun updateProximityCheck(value: Boolean) {
        _proximityCheck.value = value
    }

    fun saveAddress() {
        // Logic to save address
    }

    fun resetAddress() {
        // Logic to reset address fields
        _address1.value = ""
        _address2.value = ""
        _city.value = ""
        _state.value = ""
        _postal.value = ""
        _country.value = ""
        _proximityCheck.value = false
    }
}
