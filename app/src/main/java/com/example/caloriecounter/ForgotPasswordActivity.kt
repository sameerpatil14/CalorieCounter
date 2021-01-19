package com.example.caloriecounter

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val TAG: String = "ForgotPassword"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val changePassword = findViewById<Button>(R.id.changePassword)
        changePassword.setOnClickListener {
            changeYourPassword()
        }
        val backToSignIn = findViewById<Button>(R.id.backToSignIn)
        backToSignIn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun changeYourPassword() {
        if (!validateEmail()) {
            return
        } else {
            val relativeLayoutPb = findViewById<RelativeLayout>(R.id.relativeLayoutPb)
            val label = findViewById<TextView>(R.id.label)
            val email = findViewById<TextInputLayout>(R.id.email)

            //ProgressBar
            relativeLayoutPb.visibility = View.VISIBLE
            //Label
            label.visibility = View.VISIBLE

            auth = FirebaseAuth.getInstance()

            val stringEmail = email!!.editText!!.text.toString()

            auth.sendPasswordResetEmail(stringEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
                        label.text = "Password Reset email has been sent to your email id"
                    } else
                        label.text = task.exception!!.message
                }
            relativeLayoutPb.visibility = View.GONE
        }
    }

    //Email Validations
    private fun validateEmail(): Boolean {
        val email = findViewById<TextInputLayout>(R.id.email)
        var stringEmail = email!!.editText!!.text.toString()
        if (stringEmail.isEmpty()) {
            email.error = "Field cannot be empty!"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(stringEmail).matches()) {
            email.error = "Enter valid email"
            return false
        } else {
            email.error = null
            email.isErrorEnabled = false
            return true
        }
    }
}