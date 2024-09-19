package com.app.tensquare.ui.customview

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.app.tensquare.R

class EditTextPoppinsRegular : androidx.appcompat.widget.AppCompatEditText {
    constructor(context: Context) : super(context) {
        setFontStyle(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setFontStyle(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setFontStyle(context)
    }

    private fun setFontStyle(context: Context?) {
        val typeface = ResourcesCompat.getFont(context!!, R.font.poppins_regular)
        setTypeface(typeface)
    }
}