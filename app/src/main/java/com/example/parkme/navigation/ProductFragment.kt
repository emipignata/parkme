package com.example.parkme.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.parkme.R
import com.example.parkme.databinding.FragmentProductBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.Pago
import com.example.parkme.entities.Reserva
import com.example.parkme.viewmodels.CheckoutViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.pay.PayClient
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.WalletConstants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale


class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding
    val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val model: CheckoutViewModel by viewModels()
    private val args: ProductFragmentArgs by navArgs()
    private val addToGoogleWalletRequestCode = 1000
    //private val pago: Pago by lazy { args.pago }
    private val reserva: Reserva by lazy { args.reserva }
    private lateinit var horaSalida : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.googlePayButton.setOnClickListener {
            requestPayment()
        }

        binding.productTitle.text = reserva.direccion
        binding.productPrice.text = "\$pago.precio.toString()"
        binding.productDescription.text = "Detalle de la operacion: \n Desde: ${reserva.horaEntrada}hs \n" +
                " Hasta: ${mostrarHoraFinalizacion()}hs"
        binding.productPrice.text = calculateTotal().toString()
        Glide.with(this)
            .load(reserva.urlImage)
            .into(binding.productImage)

        // Observe ViewModel LiveData or StateFlow and update UI accordingly
        // viewModel.state.observe(viewLifecycleOwner) { state ->
        //     update UI based on state
        // }

        //        findNavController().navigate(R.id.action_productFragment_to_successFragment)
        lifecycleScope.launch {
            model.state.collect { state ->
                handleState(state)
            }
        }
    }

    private fun calculateTotal(): Float {
        var horaInicio = reserva.horaSalida
        var horaFin = reserva.horaEntrada
        return reserva.precio * (parseHoursAndMinutesToFloat(horaInicio) - parseHoursAndMinutesToFloat(horaFin))
    }

    fun parseHoursAndMinutesToFloat(timeString: String): Float {
        val parts = timeString.split(":")
        if (parts.size == 2) {
            try {
                val hours = parts[0].toFloat()
                val minutes = parts[1].toFloat() / 60.0f
                return hours + minutes
            } catch (e: NumberFormatException) {
                println("Error parsing time: $e")
            }
        } else if(timeString.equals("Indefinido")) {
            //mira esa recursividad papaaa!!
            return parseHoursAndMinutesToFloat(extractHour(Timestamp.now().toDate().toString()))
        }else{
            return parts[0].toFloat()
        }

        return 0.0f // Default value if parsing fails
    }
    private fun mostrarHoraFinalizacion(): String {
        if(reserva.horaSalida.equals("Indefinido")){
            horaSalida = extractHour(Timestamp.now().toDate().toString())
        }
        else {
            horaSalida = reserva.horaSalida
        }
        return horaSalida
    }

    private fun handleState(state: CheckoutViewModel.State) {
        if (state.checkoutSuccess) {
            setReservaState()
            setCocheraState()
            setUserState(reserva.estado)
            findNavController().popBackStack(R.id.historialFr,false)
        }

    }

    private fun setUserState(estadoReserva : String) {
        val docRef = uid?.let { db.collection("users").document(it) }
        if (docRef != null) {
            if(estadoReserva.equals("Reservada")){
                docRef.update("reservaInReservada","")
            }
            if(estadoReserva.equals("CheckIn")){
                docRef.update("reservaInCheckIn","")
            }
        }
    }

    private fun setCocheraState() {
        val cochera = db.collection("cocheras").document(reserva.cocheraId)
        cochera
            .update("available", true)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

                println("Error updating document: $e")
            }
    }
    // Add methods to update UI based on state if needed

    fun extractHour(dateString: String): String {
        val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)

        val date = originalFormat.parse(dateString)

        val hourFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

        return hourFormat.format(date)
    }

    private fun setReservaState(){
        reserva.horaSalida = extractHour(Timestamp.now().toDate().toString())
        reserva.estado = "Finalizada"
        val updates = mapOf(
            "estado" to reserva.estado,
            "horaSalida" to reserva.horaSalida
        )

        val docRef = db.collection("historial").document(reserva.reservaId)
        docRef.update(updates)
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }

    }


    private fun requestPayment() {
        model.setGooglePayButtonClickable(false)


        val dummyPriceCents = 100L
        val shippingCostCents = 900L
        val task = model.getLoadPaymentDataTask(dummyPriceCents + shippingCostCents)

        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                completedTask.result.let(::handlePaymentSuccess)
            } else {
                when (val exception = completedTask.exception) {
                    is ResolvableApiException -> {
                        resolvePaymentForResult.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    }

                    is ApiException -> {
                        handleError(exception.statusCode, exception.message)
                    }

                    else -> {
                        handleError(
                            CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                                    " exception when trying to deliver the task result to an activity!"
                        )
                    }
                }
            }

            model.setGooglePayButtonClickable(true)
        }
    }

    private val resolvePaymentForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                ComponentActivity.RESULT_OK ->
                    result.data?.let { intent ->
                        PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                    }

                WalletConstants.RESULT_ERROR -> {
                    val status = AutoResolveHelper.getStatusFromIntent(result.data)
                }

                ComponentActivity.RESULT_CANCELED -> {
                }
            }
        }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson()

        try {
            val paymentMethodData =
                JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")

            Toast.makeText(
                context,
                getString(R.string.payments_show_name, billingName),
                Toast.LENGTH_LONG
            ).show()

            Log.d(
                "Google Pay token", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
            )

            model.checkoutSuccess()

        } catch (error: JSONException) {
            Log.e("handlePaymentSuccess", "Error: $error")
        }
    }

    private fun handleError(statusCode: Int, message: String?) {
        Log.e("Google Pay API error", "Error code: $statusCode, Message: $message")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == addToGoogleWalletRequestCode) {
            when (resultCode) {
                ComponentActivity.RESULT_OK -> Toast
                    .makeText(
                        context,
                        getString(R.string.add_google_wallet_success),
                        Toast.LENGTH_LONG
                    )
                    .show()

                ComponentActivity.RESULT_CANCELED -> {
                }

                PayClient.SavePassesResult.SAVE_ERROR -> data?.let { intentData ->
                    val apiErrorMessage =
                        intentData.getStringExtra(PayClient.EXTRA_API_ERROR_MESSAGE)
                    handleError(resultCode, apiErrorMessage)
                }

                else -> handleError(
                    CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                            " exception when trying to deliver the task result to an activity!"
                )
            }

            model.setGoogleWalletButtonClickable(true)

        }
    }

}