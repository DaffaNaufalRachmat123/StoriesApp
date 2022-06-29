package com.storiesapp.common.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.ContextCompat
import com.github.ajalt.timberkt.d
import com.google.android.material.textfield.TextInputEditText
import com.storiesapp.common.R

class CustomPasswordEditText : TextInputEditText {
    private var listener : OnValidateListener? = null
    fun setValidateListener(listener : OnValidateListener){
        this.listener = listener
    }
    constructor(context: Context) : super(
        context,
        null
    )

    constructor(context: Context, attrs: AttributeSet) : super(
        context,
        attrs
    ) {
        applyValidateText()
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        applyValidateText()
    }
    private fun applyValidateText(){
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let {
                    if(it.toString().length < 6){
                        listener?.onValidate(false)
                    } else {
                        listener?.onValidate(true)
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
    interface OnValidateListener {
        fun onValidate(isError : Boolean)
    }
}