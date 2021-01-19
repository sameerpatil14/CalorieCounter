package com.example.caloriecounter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caloriecounter.helperClass.FoodItemHelperClass
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class addFoodItemActivity : AppCompatActivity() {
    val TAG = "addFoodItemActivity"

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private lateinit var foodName: TextInputLayout
    private lateinit var foodCal: TextInputLayout
    private lateinit var foodQty: TextInputLayout
    private lateinit var submit: Button

    var date: String? = ""
    var totalConsumedCal = 0
    var stringConsumedTotalCal = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food_item)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        date = SimpleDateFormat("dd-M-yyyy", Locale.getDefault()).format(Date())
        reference = database.getReference("users").child("${auth.uid}").child("date").child("$date")

        foodName = findViewById(R.id.foodName)
        foodCal = findViewById(R.id.foodCal)
        foodQty = findViewById(R.id.foodQty)
        submit = findViewById(R.id.submit)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                stringConsumedTotalCal = snapshot.child("consumedCal").getValue().toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, error.message)
            }
        })

        submit.setOnClickListener {
            takeData()
        }
    }

    private fun takeData() {
        if (!validateName() or !validateCal() or !validateQty()) {
            return
        } else {
            submitData()
        }
    }

    private fun submitData() {
        val user = auth.currentUser
        val UserId = user!!.uid
        reference = database.getReference("users").child(UserId).child("date").child("$date")


        val stringFoodName = foodName.editText!!.text.toString()
        val stringFoodCal = foodCal.editText!!.text.toString()
        val stringFoodQty = foodQty.editText!!.text.toString()

        val foodItemHelperClass =
            FoodItemHelperClass(stringFoodName, stringFoodCal, stringFoodQty)

        reference.child("foodItems").child("${System.currentTimeMillis()}$stringFoodName").setValue(foodItemHelperClass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onBackPressed()
                    totalConsumedCal =
                        stringConsumedTotalCal.toInt() + stringFoodCal.toInt() * stringFoodQty.toInt()
                    reference.child("consumedCal").setValue(totalConsumedCal.toString())
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }


    //name validations
    private fun validateName(): Boolean {
        val x = foodName!!.editText!!.text.toString()

        if (x.isEmpty()) {
            foodName.error = "Field cannot be Empty"
            return false
        } else {
            foodName.error = null
            foodName.isErrorEnabled = false
            return true
        }
    }

    //Calories validations
    private fun validateCal(): Boolean {
        val x = foodCal!!.editText!!.text.toString()

        if (x.isEmpty()) {
            foodCal.error = "Field cannot be Empty"
            return false
        } else {
            foodCal.error = null
            foodCal.isErrorEnabled = false
            return true
        }
    }

    //Quantity validations
    private fun validateQty(): Boolean {
        val x = foodQty!!.editText!!.text.toString()

        if (x.isEmpty()) {
            foodQty.error = "Field cannot be Empty"
            return false
        } else {
            foodQty.error = null
            foodQty.isErrorEnabled = false
            return true
        }
    }

}