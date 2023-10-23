package com.example.parkme.models

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

class AutocompleteEditText : AppCompatAutoCompleteTextView {

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    private fun init() {
        this.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                Log.d("MAPA", "Got focus")
            } else {
                Log.d("MAPA", "Lost focus")
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                requestFocus()
                return true
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
