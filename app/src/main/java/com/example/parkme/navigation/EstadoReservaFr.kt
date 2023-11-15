package com.example.parkme.navigation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.parkme.databinding.FragmentEstadoReservaBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Pago
import com.example.parkme.entities.Reserva
import com.example.parkme.entities.User
import com.example.parkme.viewmodels.ReservaViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class EstadoReservaFr : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val args: EstadoReservaFrArgs by navArgs()
    private lateinit var binding: FragmentEstadoReservaBinding
    private val reserva: Reserva by lazy { args.reserva }
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private var owner: User? = null
    private var cochera: Cochera? = null
    private lateinit var viewModel: ReservaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ReservaViewModel::class.java)
        binding = FragmentEstadoReservaBinding.inflate(inflater, container, false)
        viewModel.reservaState.observe(viewLifecycleOwner) { updatedReserva ->
            updateUIBasedOnReservaState(updatedReserva)
        }
        getUserState()
        setupUI()
        findOwner(uid.toString())
        findOwner(reserva.ownerId)
        findCochera(reserva.cocheraId)
        binding.DetailOwnerName.text = reserva.ownerName
        binding.DetailDescripcion.text = reserva.fechaCreacion
        binding.cantHsDetailPlaceHolder.text = "${reserva.horaEntrada} - ${reserva.horaSalida}"
        binding.DetailCallButton.setOnClickListener {
            initiateCall("123456789")
        }
        return binding.root
    }

    private fun updateUIBasedOnReservaState(reserva: Reserva) {
        when (reserva.estado) {
            "Reservada" -> setupReservada()
            "CheckIn" -> setupCheckIn()
            "CheckOut" -> setupCheckOut()
            "Finalizada" -> setupFinalizada()
        }
    }

    private fun setupUI() {
        if (reserva.estado.equals("Reservada")) {
            setupReservada()
        } else if (reserva.estado.equals("CheckIn")){
            setupCheckIn()
        } else if (reserva.estado.equals("CheckOut")){
            setupCheckOut()
        } else if (reserva.estado.equals("Finalizada")){
            setupFinalizada()
        }
    }

    private fun setupReservada(){
        binding.pagarReserva.text = "Ingresar a la Cochera"
        binding.pagarReserva.setOnClickListener {
            setUserState()
            setupReservaState("CheckIn")
        }
    }

    private fun setupCheckIn(){
        binding.pagarReserva.text = "Salir de la Cochera"
        binding.pagarReserva.setOnClickListener {
            reserva.horaSalida = extractHour(com.google.firebase.Timestamp.now().toDate().toString())
            setUserState()
            setupReservaState("CheckOut")
        }
    }

    private fun setupCheckOut(){
        binding.pagarReserva.text = "Pagar"
        binding.pagarReserva.setOnClickListener {
            val pago = Pago(reserva, reserva.ownerId, reserva.fechaCreacion, reserva.fechaCreacion,
                reserva.fechaCreacion, 100.1)
            binding.root.findNavController().navigate(EstadoReservaFrDirections.actionEstadoReservaFrToProductFragment(pago, reserva))
        }
    }

    private fun setupFinalizada(){
        binding.pagarReserva.visibility = View.GONE
    }

    private fun setupReservaState(estado: String) {
        viewModel.updateReservaState(reserva, estado)
    }

    private fun setUserState() {
        viewModel.updateUserState(reserva.usuarioId, reserva, {
            Log.d("TAG", "DocumentSnapshot successfully updated!")
        }, { e ->
            Log.w("TAG", "Error updating document", e)
        })
    }

    private fun getUserState() {
        if (uid!= null) {
            viewModel.getUserState(uid, {
                Log.d("TAG", "DocumentSnapshot successfully updated!")
            }, { e ->
                Log.w("TAG", "Error updating document", e)
            })
        }
    }

    private fun extractHour(dateString: String): String {
        val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val date = originalFormat.parse(dateString)
        val hourFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        return hourFormat.format(date)
    }

    private fun initiateCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    private fun findCochera(cocheraId: String) {
        db.collection("cocheras").document(cocheraId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    cochera = documentSnapshot.toObject(Cochera::class.java)
                    cochera?.let {
                        updateCocheraUI(it)
                    }
                } else {
                    Log.e("EstadoReservaFr", "Cochera not found.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("EstadoReservaFr", "Error getting documents: ", exception)
            }
    }

    private fun updateCocheraUI(cochera: Cochera) {
        binding.DetailLocation.text = cochera.direccion
        binding.DetailName.text = cochera.nombre
        binding.precioPorHoraDetail.text = cochera.price.toString()
        binding.DetailOwnerName.text = cochera.ownerName
        binding.DetailDescripcion.text = cochera.descripcion
    }

    private fun findOwner(ownerId: String) {
        db.collection("users").document(ownerId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    owner = documentSnapshot.toObject(User::class.java)
                    // User-related UI updates can be done here if needed
                } else {
                    Log.e("EstadoReservaFr", "User not found.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("EstadoReservaFr", "Error getting documents: ", exception)
            }
    }
}
