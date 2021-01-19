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
import com.example.caloriecounter.DashboardActivity
import com.example.caloriecounter.ForgotPasswordActivity
import com.example.caloriecounter.ProfileActivity
import com.example.caloriecounter.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.*

class FragmentSignIn : Fragment() {

    val TAG = "FragmentSignIn"
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

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

        return inflater!!.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

        val signIn = activity?.findViewById<Button>(R.id.signIn)
        signIn?.setOnClickListener {
            SignInUser()
        }

        val forgotPassword = activity?.findViewById<Button>(R.id.forgotPassword)
        forgotPassword?.setOnClickListener {
            forgotYourPassword()
        }
    }

    private fun forgotYourPassword() {
        startActivity(Intent(activity, ForgotPasswordActivity::class.java))
    }

    private fun SignInUser() {
        if (!validateEmail() or !validatePasword()) {
            return
        } else {
            LogUserIn()
        }
    }

    private fun LogUserIn() {
        val relativeLayoutPb = activity!!.findViewById<RelativeLayout>(R.id.relativeLayoutPb)

        //ProgressBar
        relativeLayoutPb.visibility = View.VISIBLE

        val email = activity?.findViewById<TextInputLayout>(R.id.email)
        val password = activity?.findViewById<TextInputLayout>(R.id.password)

        var stringEmail = email!!.editText!!.text.toString()
        var stringPassword = password!!.editText!!.text.toString()

        auth = FirebaseAuth.getInstance();
        activity?.let {
            auth.signInWithEmailAndPassword(stringEmail, stringPassword)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signUserWithEmailPasword:success")
                        email.error = null
                        email.isErrorEnabled = false

                        val user = auth.currentUser
//                        var userId = user!!.uid

                        if (user!!.isEmailVerified()) {

                            database = FirebaseDatabase.getInstance()
                            reference = database.getReference("users").child(user.uid)
                            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val stringHeight =
                                        snapshot.child("height").getValue().toString()
                                    val stringWeight =
                                        snapshot.child("weight").getValue().toString()
                                    val stringBmr = snapshot.child("bmr").getValue().toString()
                                    val stringAge = snapshot.child("age").getValue().toString()
                                    if ((stringHeight == "") or (stringWeight == "") or (stringAge == "") or (stringBmr == "")) {
                                        startActivity(Intent(activity, ProfileActivity::class.java))
                                        Toast.makeText(
                                            activity,
                                            "Logged In\nComplete your profile",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        activity!!.finishAffinity()
                                    } else {
                                        startActivity(
                                            Intent(
                                                activity,
                                                DashboardActivity::class.java
                                            )
                                        )
                                        Toast.makeText(activity, "Logged In", Toast.LENGTH_SHORT)
                                            .show()
                                        activity!!.finishAffinity()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d(TAG,"onCancelled")
                                }

                            })

                        } else {
                            user.sendEmailVerification()
                            Toast.makeText(
                                activity,
                                "Email not Verified \nVerification Email sent again",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                        relativeLayoutPb.visibility = View.GONE

                    } else {
                        relativeLayoutPb.visibility = View.GONE
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)

                        try {
                            throw task.exception!!
                        } catch (invalidEmail: FirebaseAuthInvalidUserException) { //invalid email
                            email.error = "Email Id doesn't exist"
                            email.requestFocus()
                        } catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                            password.error = "Wrong Password"
                            password.requestFocus()
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


    //Password Validations
    private fun validatePasword(): Boolean {
        val password = activity?.findViewById<TextInputLayout>(R.id.password)
        var stringPassword = password!!.editText!!.text.toString()
        if (stringPassword.isEmpty()) {
            password.error = "Field cannot be empty!"
            return false
        } else {
            password.error = null
            password.isErrorEnabled = false
            return true
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null && currentUser.isEmailVerified()) {
            startActivity(Intent(activity, DashboardActivity::class.java))
            activity!!.finishAffinity()
        }
    }
}