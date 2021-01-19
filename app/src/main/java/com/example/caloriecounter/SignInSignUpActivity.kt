package com.example.caloriecounter

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caloriecounter.fragment.FragmentSignIn
import com.example.caloriecounter.fragment.FragmentSignUp

class SignInSignUpActivity : AppCompatActivity() {

    var isFragmentSignInLoaded = true
    val manager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_sign_up)

        val fragmentChanger = findViewById<Button>(R.id.fragmentChanger)

        showFragmentSignIn()
        fragmentChanger.setOnClickListener {
            if (isFragmentSignInLoaded) {
                fragmentChanger.text = "Already have an account? SignIn"
                showFragmentSignUp()
            } else {
                onBackPressed()
            }
        }
    }

    fun showFragmentSignIn() {
        val transaction = manager.beginTransaction()
        val fragment = FragmentSignIn()
        transaction.replace(R.id.fragmentHolder, fragment)
        transaction.commit()
        isFragmentSignInLoaded = true
    }

    fun showFragmentSignUp() {
        val transaction = manager.beginTransaction()
        val fragment = FragmentSignUp()
        transaction.replace(R.id.fragmentHolder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        isFragmentSignInLoaded = false
    }

    //Exit if back button is clicked twice
    private var back_pressed_time: Long = 0

    override fun onBackPressed() {
        if (isFragmentSignInLoaded == false) {
            super.onBackPressed()
            isFragmentSignInLoaded = true
            val fragmentChanger = findViewById<Button>(R.id.fragmentChanger)
            fragmentChanger.text = "New User? SignUp"
        } else if (isFragmentSignInLoaded == true) {
            if (back_pressed_time + 2000 > System.currentTimeMillis()) {
                super.onBackPressed()
            } else Toast.makeText(baseContext, "Press once again to exit!", Toast.LENGTH_SHORT)
                .show()
            back_pressed_time = System.currentTimeMillis()
        }
    }
}