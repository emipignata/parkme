package com.example.parkme.navigation

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerFr(val listener: (String) -> Unit): DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val picker = TimePickerDialog(requireContext(), this, hour, minute, true)

        return picker
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        if(hourOfDay < 10 && minute < 10 ){
            listener("0$hourOfDay:0$minute")
        }else if(hourOfDay < 10){
            listener("0$hourOfDay:$minute")
        }else if(minute < 10){
            listener("$hourOfDay:0$minute")
        }else{
            listener("$hourOfDay:$minute")
        }

    }

}