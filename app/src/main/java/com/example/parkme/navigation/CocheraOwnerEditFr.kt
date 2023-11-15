package com.example.parkme.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.parkme.MainActivity
import com.example.parkme.R
import com.example.parkme.databinding.DialogAddTimeRangeBinding
import com.example.parkme.databinding.FragmentCocheraOwnerEditBinding
import com.example.parkme.entities.Cochera
import com.example.parkme.entities.DailyAvailability
import com.example.parkme.entities.TimeRange
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.UUID

@SuppressLint("StaticFieldLeak")
private val db = FirebaseFirestore.getInstance()
private val uid = FirebaseAuth.getInstance().currentUser?.uid

class CocheraOwnerEditFr : Fragment() {

    private val args: CocheraOwnerEditFrArgs by navArgs()
    private lateinit var binding: FragmentCocheraOwnerEditBinding
    private val cochera: Cochera by lazy { args.cochera }
    private val uid: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }
    private var storageReference = FirebaseStorage.getInstance().reference
    private var imageURL: String = ""
    companion object {
        private val TAG = CocheraOwnerEditFr::class.java.simpleName
        private const val PICK_IMAGE_REQUEST = 1
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCocheraOwnerEditBinding.inflate(inflater, container, false)
        var cocheraNombre = binding.eTNombreCocheraItemView
        cocheraNombre.setText(cochera.nombre)
        var cocheraDireccion = binding.eTDireccionItemView
        cocheraDireccion.setText(cochera.direccion)
        var cocheraPrecio = binding.eTPrecioPorHoraItemView
        cocheraPrecio.setText(cochera.price.toString())
        var imgButton = binding.simpleImageButtonItemView
        var aceptarEditButton: Button = binding.buttonAceptarEdit
        var cocheraDescripcion = binding.eTDescripcionItemView
        cocheraDescripcion.setText(cochera.descripcion)
        if (activity is MainActivity) {
            (activity as MainActivity).setBottomNavViewVisibility(View.GONE)
        }

        var imgCochera = binding.simpleImageButtonItemView
        updateImagegButtonContent(cochera.urlImage, imgCochera)


        binding.switch247Editar.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setFullWeekAvailability()
            } else {
                clearFullWeekAvailability()
            }
            toggleTimeRangeViews(isChecked)
        }

        binding.imgAddDateTimeRangeEdit.setOnClickListener {
            addTimeRange()
        }

        fun updateButtonState() {
            val isNombreCocheraValid = cocheraNombre.text?.isNotEmpty()
            val isPrecioPorHoraValid = cocheraPrecio.text?.toString()?.toFloatOrNull() != null
            val isDescripcionValid = cocheraDescripcion.text?.isNotEmpty()

            aceptarEditButton.isEnabled =
                isNombreCocheraValid == true && isPrecioPorHoraValid && isDescripcionValid == true
        }

        cocheraNombre.addTextChangedListener { updateButtonState() }
        cocheraPrecio.addTextChangedListener { updateButtonState() }
        cocheraDescripcion.addTextChangedListener { updateButtonState() }

        imgButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, CocheraOwnerEditFr.PICK_IMAGE_REQUEST)
        }


        aceptarEditButton.setOnClickListener() {

            val nuevoNombre = cocheraNombre.text.toString()
            val nuevaDireccion = cocheraDireccion.text.toString()
            val nuevoPrecio = cocheraPrecio.text.toString().toDouble()

            cochera.nombre = nuevoNombre
            cochera.direccion = nuevaDireccion
            cochera.price = nuevoPrecio.toFloat()
            cochera.urlImage =  if(imageURL.equals("")) cochera.urlImage else imageURL

            db.collection("cocheras").document(cochera.cocheraId).set(cochera)
            Toast.makeText(requireContext(), "La cochera ${cochera.nombre} se ha editada con éxito", Toast.LENGTH_SHORT)
                .show()
            binding.root.findNavController().navigateUp()
            binding.root.findNavController().navigateUp()
        }


        return binding.root

    }

    private fun updateImagegButtonContent(urlImage: String, imgCochera : ImageView ) {
        Glide
            .with(binding.root.context)
            .load(urlImage).circleCrop()
            .into(imgCochera)
    }


    private fun toggleTimeRangeViews(isFullWeek: Boolean) {
        binding.imgAddDateTimeRangeEdit.visibility = if (isFullWeek) View.GONE else View.VISIBLE
        binding.dividerAddDateTimeRange.visibility = if (isFullWeek) View.GONE else View.VISIBLE
        binding.textAddDateTimeRangeEdit.visibility = if (isFullWeek) View.GONE else View.VISIBLE
        binding.disponibilidadContainer.visibility = if (isFullWeek) View.GONE else View.VISIBLE
    }

    private fun setFullWeekAvailability() {
        val midnight = LocalTime.MIDNIGHT.toSecondOfDay().toLong()
        val endOfDay = LocalTime.MAX.toSecondOfDay().toLong()
        val fullWeekAvailability = listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo")
            .map { DailyAvailability(it, mutableListOf(TimeRange(midnight, endOfDay))) }
        cochera.weeklyAvailability.clear()
        cochera.weeklyAvailability.addAll(fullWeekAvailability)
    }

    private fun clearFullWeekAvailability() {
        cochera.weeklyAvailability.clear()
    }

    private fun addTimeRange() {
        val dialogBinding = DialogAddTimeRangeBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBinding.timePickerStart.setIs24HourView(true)
        dialogBinding.timePickerEnd.setIs24HourView(true)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Agregar Disponibilidad")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { dialog, _ ->
                val selectedDay = dialogBinding.spinnerDayOfWeek.selectedItem.toString()
                val startTime = LocalTime.of(dialogBinding.timePickerStart.hour, dialogBinding.timePickerStart.minute)
                val endTime = LocalTime.of(dialogBinding.timePickerEnd.hour, dialogBinding.timePickerEnd.minute)
                addToWeeklyAvailability(selectedDay, startTime, endTime)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()
        dialog.show()
    }

    private fun addToWeeklyAvailability(day: String, start: LocalTime, end: LocalTime) {
        val startSeconds = start.toSecondOfDay().toLong()
        val endSeconds = end.toSecondOfDay().toLong()
        val newTimeRange = TimeRange(startSeconds, endSeconds)

        val dailyAvailability = cochera.weeklyAvailability.find { it.dayOfWeek == day }
        if (dailyAvailability != null) {
            if (!dailyAvailability.timeRanges.any { it.startSeconds == startSeconds && it.endSeconds == endSeconds }) {
                dailyAvailability.timeRanges.add(newTimeRange)
            }
        } else {
            val newDailyAvailability = DailyAvailability(day, mutableListOf(newTimeRange))
            cochera.weeklyAvailability.add(newDailyAvailability)
        }
        addTimeRangeView(newTimeRange, day)
    }

    private fun addTimeRangeView(timeRange: TimeRange, day: String) {
        val timeRangeView = LayoutInflater.from(context).inflate(R.layout.time_range_item, null)

        val txtDay = timeRangeView.findViewById<TextView>(R.id.txtDay)
        val txtStartTime = timeRangeView.findViewById<TextView>(R.id.txtStartTime)
        val txtEndTime = timeRangeView.findViewById<TextView>(R.id.txtEndTime)
        val btnDelete = timeRangeView.findViewById<Button>(R.id.btnDelete)

        txtDay.text = day
        txtStartTime.text = LocalTime.ofSecondOfDay(timeRange.startSeconds).toString()
        txtEndTime.text = LocalTime.ofSecondOfDay(timeRange.endSeconds).toString()

        btnDelete.setOnClickListener {
            removeTimeRangeView(timeRangeView, timeRange, day)
        }

        timeRangeView.setTag(R.id.tag_daily_availability, DailyAvailability(day, mutableListOf(timeRange)))
        binding.disponibilidadContainer.addView(timeRangeView)
    }

    private fun removeTimeRangeView(view: View, timeRange: TimeRange, day: String) {
        // Remove the view from the layout
        binding.disponibilidadContainer.removeView(view)

        // Find the DailyAvailability object for the specified day
        val dayAvailability = cochera.weeklyAvailability.find { it.dayOfWeek == day }

        // Remove the specific TimeRange object from the DailyAvailability
        dayAvailability?.timeRanges?.removeIf { it.startSeconds == timeRange.startSeconds && it.endSeconds == timeRange.endSeconds }

        // If there are no more TimeRanges left for that day, remove the DailyAvailability object from the list
        if (dayAvailability?.timeRanges.isNullOrEmpty()) {
            cochera.weeklyAvailability.remove(dayAvailability)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CocheraOwnerEditFr.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            imageUri?.let {
                uploadImageToFirebaseStorage(it)
            }
        }
    }
    private fun uploadImageToFirebaseStorage(uri: Uri) {
        val filename = UUID.randomUUID().toString()
        val ref = storageReference.child("images/$filename")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveImageURLToFirestore(downloadUri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }
    private fun saveImageURLToFirestore(url: String) {
        val data = hashMapOf("imageUrl" to url)
        db.collection("images")
            .add(data)
            .addOnSuccessListener { documentReference ->
                if (isAdded && !isRemoving) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        updateImagegButtonContent(url,binding.simpleImageButtonItemView)
                    }
                }
                imageURL = url
            }
            .addOnFailureListener {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to save image URL", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
