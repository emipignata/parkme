package com.example.parkme.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.parkme.R
import com.example.parkme.databinding.FragmentProgramarReservaBinding

class ProgramarReservaFr : Fragment() {
    private lateinit var binding : FragmentProgramarReservaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProgramarReservaBinding.inflate(inflater, container, false)
        binding.etProgramarReservaFechaDesde.setOnClickListener {
            showDatePickerDialog()
        }
        binding.etProgramarReservaFechaHasta.setOnClickListener {
            showDatePickerDialog()
        }

        binding.etProgramarReservaHoraDesde.setOnClickListener {
            showTimePickerDialog()
        }
        binding.etProgramarReservaHoraHasta.setOnClickListener {
            showTimePickerDialog()
        }

        binding.btnProgramarReservaConfirmar.setOnClickListener {
            confirmarReserva()
        }
        return binding.root
    }

    private fun showDatePickerDialog() {
     val datePicker = DatePickerFr { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(childFragmentManager, "datePicker")
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFr { onTimeSelected(it) }
        timePicker.show(childFragmentManager, "timePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        binding.etProgramarReservaFechaDesde.setText("$day/$month/$year")
    }

    private fun onTimeSelected(time: String) {
        binding.etProgramarReservaHoraDesde.setText(time)
    }

    private fun confirmarReserva() {
        val navController = binding.root.findNavController()
        navController.popBackStack(R.id.navigation_container, false)
        navController.navigate(R.id.historialFr)
    }

}