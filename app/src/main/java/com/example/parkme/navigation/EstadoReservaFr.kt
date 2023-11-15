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
import com.example.parkme.entities.Reserva
import com.example.parkme.entities.User
import com.example.parkme.viewmodels.ReservaViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

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
        updateUIBasedOnReservaState(reserva)
        findOwner(uid.toString())
        findOwner(reserva.ownerId)
        findCochera(reserva.cocheraId)
        binding.DetailOwnerName.text = reserva.ownerName
        binding.DetailDescripcion.text = reserva.direccion
        binding.DetailCallButton.setOnClickListener {
            initiateCall("123456789")
        }
        return binding.root
    }

    private fun updateUIBasedOnReservaState(reserva: Reserva) {
        val dateFormat = SimpleDateFormat("dd/MM/yy-HH:mm", Locale.getDefault())
        val formattedFechaEntrada = reserva.fechaEntrada?.toDate()?.let { dateFormat.format(it) } ?: "N/A"
        val formattedFechaSalida = reserva.fechaSalida?.toDate()?.let { dateFormat.format(it) } ?: "En curso"
        var timeDifference = calculateTimeDifference(reserva.fechaEntrada, Timestamp.now())
        var formattedTimeDifference : String
        when (reserva.estado) {
            "Reservada" -> {
                timeDifference = calculateTimeDifference(reserva.fechaEntrada, reserva.fechaSalida)
                formattedTimeDifference = formatTimeDifference(timeDifference)
                binding.precioTotalDetailPlaceholder.text = "Precio TotalEstimado: "
                binding.cantHsDetailPlaceHolder.text = "\n \nDesde: $formattedFechaEntrada \nHasta: $formattedFechaSalida\n" +
                        "Tiempo Estimado: $formattedTimeDifference"
                binding.pagarReserva.text = "Ingresar a la Cochera"
                binding.cantidadDeHorasDetail.text = "HorasReservadas"
                binding.precioTotalDetail.text = calculatePrice(timeDifference, reserva.precioPorHora)
                binding.pagarReserva.setOnClickListener {
                    setUserState()
                    setupReservaState("CheckIn")
                }
            }
            "CheckIn" -> {
                timeDifference = calculateTimeDifference(reserva.fechaEntrada, Timestamp.now())
                formattedTimeDifference = formatTimeDifference(timeDifference)
                binding.cantidadDeHorasDetail.text = "En Curso: "
                binding.cantHsDetailPlaceHolder.text = "\n\nDesde: $formattedFechaEntrada \nTiempo Transcurrido: $formattedTimeDifference"
                binding.precioTotalDetailPlaceholder.text = "Precio al Momento"
                binding.precioTotalDetail.text = calculatePrice(timeDifference, reserva.precioPorHora)
                binding.pagarReserva.text = "Salir de la Cochera"
                binding.pagarReserva.setOnClickListener {
                    reserva.fechaSalida = Timestamp.now()
                    setUserState()
                    setupReservaState("CheckOut")
                }
            }
            "CheckOut" -> {
                timeDifference = calculateTimeDifference(reserva.fechaEntrada, reserva.fechaSalida)
                formattedTimeDifference = formatTimeDifference(timeDifference)
                binding.precioTotalDetailPlaceholder.text = "Precio Total: "
                binding.precioTotalDetail.text = calculatePrice(timeDifference, reserva.precioPorHora)
                binding.cantidadDeHorasDetail.text = "Estadía: "
                binding.cantHsDetailPlaceHolder.text = "\n\nDesde: $formattedFechaEntrada \nHasta: $formattedFechaSalida \n Cant Hs: $formattedTimeDifference"
                binding.pagarReserva.text = "Pagar"
                binding.pagarReserva.setOnClickListener {
                    setUserState()
                    binding.root.findNavController().navigate(EstadoReservaFrDirections.actionEstadoReservaFrToProductFragment(reserva))
                }
            }
            "Finalizada" -> {
                timeDifference = calculateTimeDifference(reserva.fechaEntrada, reserva.fechaSalida)
                formattedTimeDifference = formatTimeDifference(timeDifference)
                binding.precioTotalDetailPlaceholder.text = "Precio Total: "
                binding.precioTotalDetail.text = calculatePrice(timeDifference, reserva.precioPorHora)
                binding.cantidadDeHorasDetail.text = "Estadía: "
                binding.cantHsDetailPlaceHolder.text = "\n\nDesde: $formattedFechaEntrada \nHasta: $formattedFechaSalida \n Cant Hs: $formattedTimeDifference"
                binding.pagarReserva.visibility = View.GONE
            }
        }
    }

    private fun formatTimeDifference(timeDifferenceInMinutes: Long): String {
        val hours = timeDifferenceInMinutes / 60
        val minutes = timeDifferenceInMinutes % 60
        return String.format("%02d:%02d", hours, minutes)
    }

    private fun calculatePrice(timeDifferenceInMinutes: Long, hourlyRate: Float): String {
        if (timeDifferenceInMinutes <= 60) {
            return String.format("%.2f", hourlyRate)
        } else {
            val additionalMinutes = timeDifferenceInMinutes - 60
            val additionalQuarters = Math.ceil(additionalMinutes / 15.0).toInt()
            val additionalCharge = hourlyRate / 4 * additionalQuarters
            val totalCharge = hourlyRate + additionalCharge
            return String.format("%.2f", totalCharge)
        }
    }

    private fun calculateTimeDifference(startTimestamp: Timestamp?, endTimestamp: Timestamp?): Long {
        startTimestamp?.toDate()?.let { startDate ->
            val endDate = endTimestamp?.toDate()
            val diffInMillis = endDate!!.time - startDate.time
            return TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        }
        return 0L
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
