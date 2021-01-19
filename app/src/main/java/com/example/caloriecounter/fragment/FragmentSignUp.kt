package com.example.caloriecounter.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.caloriecounter.R
import com.example.caloriecounter.SignInSignUpActivity
import com.example.caloriecounter.helperClass.UserHelperClass
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class FragmentSignUp : Fragment() {

    val TAG = "FragmentSignUp"
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    var stringName: String = ""
    var stringEmail: String = ""
    var stringWeight: String = ""
    var stringHeight: String = ""
    var stringAge: String = ""
    var stringGender: String = "Male"
    var stringBmi: String = ""
    var stringPassword: String = ""
    var stringBmr: String = ""

//    var database = FirebaseDatabase.getInstance()
//    var reference = database.getReference("message")

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")

        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

        val signUp = activity?.findViewById<Button>(R.id.signUp)
        signUp?.setOnClickListener {
            SignUpUser()
        }

    }

    private fun SignUpUser() {
        if (!validateName() or !validateEmail() or !validatePasword()) {
            return
        } else {

            registerUser()
        }
    }

    private fun registerUser() {
        val relativeLayoutPb = activity!!.findViewById<RelativeLayout>(R.id.relativeLayoutPb)

        //ProgressBar
        relativeLayoutPb.visibility = View.VISIBLE

        val email = activity?.findViewById<TextInputLayout>(R.id.email)
        val password = activity?.findViewById<TextInputLayout>(R.id.password)
        val name = activity?.findViewById<TextInputLayout>(R.id.name)

        stringEmail = email!!.editText!!.text.toString()
        stringPassword = password!!.editText!!.text.toString()
        stringName = name!!.editText!!.text.toString()

        auth = FirebaseAuth.getInstance();
        activity?.let {
            auth.createUserWithEmailAndPassword(stringEmail, stringPassword)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        email.error = null
                        email.isErrorEnabled = false

                        val user = auth.currentUser
                        val userId = user!!.uid

                        database = FirebaseDatabase.getInstance()
                        reference = database.getReference("users").child(userId)

                        val userHelperClass = UserHelperClass(stringName, stringEmail, stringGender, stringHeight, stringWeight, stringAge, stringBmi, stringBmr)
                        reference.setValue(userHelperClass).addOnCompleteListener { task ->

                            relativeLayoutPb.visibility = View.GONE
                            if (task.isSuccessful) {
                                val intent = Intent(activity, SignInSignUpActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(
                                    activity,
                                    "Verification Email Sent \nPlease Verify and SignIn",
                                    Toast.LENGTH_LONG
                                ).show()
                                user.sendEmailVerification()
                                activity!!.finishAffinity()

                            } else {
                                Toast.makeText(
                                    activity,
                                    task.exception!!.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        auth.signOut()
                    } else {
                        relativeLayoutPb.visibility = View.GONE
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)

                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthUserCollisionException) {
                            email.error = "Email Id already in use"
                            email.requestFocus()
                        } catch (e: Exception) {
                            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }
    }


    //Email Validations
    private fun validateEmail(): Boolean {
        val email = activity?.findViewById<TextInputLayout>(R.id.email)
        val x = email!!.editText!!.text.toString()
        if (x.isEmpty()) {
            email.error = "Field cannot be empty!"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(x).matches()) {
            email.error = "Enter valid email"
            return false
        } else {
            email.error = null
            email.isErrorEnabled = false
            return true
        }
    }


    //Password Validations
    private fun validatePasword(): Boolean {
        val password = activity?.findViewById<TextInputLayout>(R.id.password)
        val x = password!!.editText!!.text.toString()

        val noWhiteSpace = "\\s".toRegex()
        if (x.isEmpty()) {
            password.error = "Field cannot be empty!"
            return false
        } else if (x.count() < 6) {
            password.error = "Password is too weak"
            return false
        } else if (x.contains(noWhiteSpace)) {
            password.error = "No WhiteSpaces allowed"
            return false
        } else {
            password.error = null
            password.isErrorEnabled = false
            return true
        }
    }

    //name validations
    private fun validateName(): Boolean {

        val name = activity?.findViewById<TextInputLayout>(R.id.name)
        val x = name!!.editText!!.text.toString()

        if (x.isEmpty()) {
            name.error = "Field cannot be Empty"
            return false
        } else {
            name.error = null
            name.isErrorEnabled = false
            return true
        }
    }

}

