package com.app.tensquare.utils

import android.graphics.Color
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern


object Validation {

    // Regular Expression
    // you can change the expression based on your need
    private val EMAIL_REGEX =
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    private val PHONE_REGEX = "\\d{3}\\d{7}"

    // Error Messages
    private val REQUIRED_MSG = "Required"
    private val INVALID_PIN_MSG = "Invalid Pincode"
    private val EMAIL_MSG = "Invalid Email ID"
    private val PHONE_MSG = "10 digits required"
    private val PASSWORD_MSG = "minimum 6 characters required"
    private val CONFIRM_PASSWORD = "Password not match"
    private val CONFIRM_ACCOUNT_NUMBER = "Account numbers don't match "

    // call2 this method when you need to check email validation
    fun isEmailAddress(editText: EditText, required: Boolean): Boolean {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required)
    }

    // call2 this method when you need to check phone number validation
    fun isPhoneNumber(editText: EditText, required: Boolean): Boolean {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required)
    }

    fun isPhoneNumber(
        editText: EditText,
        required: Boolean,
        textInputLayout: TextInputLayout,
        ErrorMsg: String?
    ): Boolean {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required, textInputLayout, ErrorMsg)
    }

    fun isValid(
        editText: EditText, regex: String?,
        errMsg: String?, required: Boolean, textInputLayout: TextInputLayout, ErrorMsg: String?
    ): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        // clearing the error, if it was previously set by some other values
        textInputLayout.error = null

        // text required and editText is blank, so return false
        if (required && !hasText(editText, textInputLayout, ErrorMsg)) return false

        // pattern doesn't match so returning false
        if (required && !regex?.let { Pattern.matches(it, text) }!!) {
            textInputLayout.error = ErrorMsg
            return false
        }
        return true
    }

    fun hasText(editText: EditText, textInputLayout: TextInputLayout, ErrorMsg: String?): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        textInputLayout.error = null

        // length 0 means there is no text
        if (text.length == 0) {
            textInputLayout.error = ErrorMsg
            return false
        }
        return true
    }

    // return true if the input field is valid, based on the parameter passed
    fun isValid(
        editText: EditText,
        regex: String?,
        errMsg: String?,
        required: Boolean
    ): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        // clearing the error, if it was previously set by some other values
        editText.error = null

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false

        // pattern doesn't match so returning false
        if (required && !regex?.let { Pattern.matches(it, text) }!!) {
            editText.error = errMsg
            return false
        }
        return true
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    fun hasText(editText: EditText): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        editText.error = null

        // length 0 means there is no text
        if (text.length == 0) {
            editText.error = REQUIRED_MSG
            return false
        }
        return true
    }

    fun hasText(textView: TextView): Boolean {
        val text = textView.text.toString().trim { it <= ' ' }
        textView.error = null

        // length 0 means there is no text
        if (text.isEmpty()) {
            textView.error = REQUIRED_MSG
            return false
        }
        return true
    }

    fun has6Characters(editText: EditText): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        editText.error = null

        // length 0 means there is no text
        if (text.length < 6) {
            editText.error = PASSWORD_MSG
            return false
        }
        return true
    }

    fun validPinCode(editText: EditText): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        editText.error = null

        // length 0 means there is no text
        if (text.length < 6) {
            editText.error = INVALID_PIN_MSG
            return false
        }
        return true
    }

    fun hasSpinnerSelected(spinner: Spinner): Boolean {
        val position = spinner.selectedItemPosition

        // length 0 means there is no text
        if (position == 0) {
            val errorText = spinner.selectedView as TextView
            errorText.error = ""
            errorText.setTextColor(Color.RED) //just to highlight that this is an error
            errorText.text = "" //changes the selected item text to this
            return false
        }
        return true
    }

    fun hasTextToast(editText: EditText): Boolean {
        val text = editText.text.toString().trim { it <= ' ' }
        editText.clearFocus()
        // length 0 means there is no text
        if (text.length == 0) {
            editText.requestFocus()
            editText.context.showToast(REQUIRED_MSG)
            return false
        }
        return true
    }

    fun isConfirmPassword(editTextPassword: EditText, editTextConfirmPasswords: EditText): Boolean {
        val password = editTextPassword.text.toString().trim { it <= ' ' }
        val confirmPassword = editTextConfirmPasswords.text.toString().trim { it <= ' ' }
        if (password != confirmPassword) {
            editTextConfirmPasswords.error = CONFIRM_PASSWORD
            return false
        }
        return true
    }

    fun isConfirmAccountNumber(
        editTextPassword: EditText,
        editTextConfirmPasswords: EditText
    ): Boolean {
        val password = editTextPassword.text.toString().trim { it <= ' ' }
        val confirmPassword = editTextConfirmPasswords.text.toString().trim { it <= ' ' }
        if (password != confirmPassword) {
            editTextConfirmPasswords.error = CONFIRM_ACCOUNT_NUMBER
            return false
        }
        return true
    }

    fun isDifferText(oldText: String, editText: EditText): Boolean {
        val newText = editText.text.toString().trim { it <= ' ' }
        return !oldText.equals(newText, ignoreCase = true)
    }

    fun isDifferText(oldText: String, editText: TextView): Boolean {
        val newText = editText.text.toString().trim { it <= ' ' }
        return !oldText.equals(newText, ignoreCase = true)
    }

    fun isDifferText(oldText: String, newText: String): Boolean {
        return !oldText.equals(newText, ignoreCase = true)
    }


    fun isCheckBoxChecked(checkBox: CheckBox): Boolean {
        if (!checkBox.isChecked) {
            checkBox.error = REQUIRED_MSG
            return false
        }
        return true
    }


}