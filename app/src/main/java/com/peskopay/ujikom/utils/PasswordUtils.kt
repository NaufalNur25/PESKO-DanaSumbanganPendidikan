package com.peskopay.ujikom.utils

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.CheckBox
import android.widget.EditText

class PasswordUtils {
    companion object {
        fun setupPasswordVisibilityToggle(
            editTextPassword: EditText,
            checkBoxShowPassword: CheckBox
        ) {
            checkBoxShowPassword.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                } else {
                    editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                }
            }
        }
    }
}