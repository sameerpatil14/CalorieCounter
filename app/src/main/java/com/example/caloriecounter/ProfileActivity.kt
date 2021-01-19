package com.example.caloriecounter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    val TAG = "ProfileActivity"

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    var stringName: String = ""
    var stringEmail: String = ""
    var stringWeight: String = ""
    var stringHeight: String = ""
    var stringAge: String = ""
    var stringGender: String = ""
    var stringBmi: String = ""
    var stringBmr: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener { onBackPressed() }

        //loads the data
        loadData()

        //updates data
        val update = findViewById<Button>(R.id.update)
        update.setOnClickListener {
            updateData()
        }
    }

    private fun loadData() {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users").child(UserId)

        val profileName = findViewById<TextView>(R.id.profileName)
        val profileEmail = findViewById<TextView>(R.id.profileEmail)
        val name = findViewById<TextInputLayout>(R.id.name)
        val age = findViewById<TextInputLayout>(R.id.age)

        val male = findViewById<RadioButton>(R.id.male)
        val female = findViewById<RadioButton>(R.id.female)

        val height = findViewById<TextInputLayout>(R.id.height)
        val weight = findViewById<TextInputLayout>(R.id.weight)
        val bmi = findViewById<TextView>(R.id.bmi)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                stringName = snapshot.child("name").getValue().toString()
                stringEmail = snapshot.child("email").getValue().toString()
                stringGender = snapshot.child("gender").getValue().toString()
                stringHeight = snapshot.child("height").getValue().toString()
                stringWeight = snapshot.child("weight").getValue().toString()
                stringAge = snapshot.child("age").getValue().toString()
                stringBmi = snapshot.child("bmi").getValue().toString()
                stringBmr = snapshot.child("bmr").getValue().toString()

                profileName.text = stringName
                profileEmail.text = stringEmail
                name.editText!!.setText(stringName)
                age.editText!!.setText(stringAge)

                if (stringGender == "Female") {
                    female.isChecked = true
                } else {
                    male.isChecked = true
                }
                height.editText!!.setText(stringHeight)
                weight.editText!!.setText(stringWeight)

                bmi.text = "Your BMI is ${stringBmi}"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, error.message)
            }
        })
    }

    //name validations
    private fun validateName(): Boolean {

        val name = findViewById<TextInputLayout>(R.id.name)
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

    //Height validations
    private fun validateHeight(): Boolean {

        val height = findViewById<TextInputLayout>(R.id.height)
        val x = height!!.editText!!.text.toString()

        if (x.isEmpty()) {
            height.error = "Field cannot be Empty"
            return false
        } else if (x.toFloat() < 1) {
            height.error = "Height cannot be less than 1"
            return false
        } else {
            height.error = null
            height.isErrorEnabled = false
            return true
        }
    }

    //Weight validations
    private fun validateWeight(): Boolean {

        val weight = findViewById<TextInputLayout>(R.id.weight)
        val x = weight!!.editText!!.text.toString()

        if (x.isEmpty()) {
            weight.error = "Field cannot be Empty"
            return false
        } else if (x.toFloat() <= 0) {
            weight.error = "Weight cannot be less than 1"
            return false
        } else {
            weight.error = null
            weight.isErrorEnabled = false
            return true
        }
    }

    //Age validations
    private fun validateAge(): Boolean {

        val age = findViewById<TextInputLayout>(R.id.age)
        val x = age!!.editText!!.text.toString()

        if (x.isEmpty()) {
            age.error = "Field cannot be Empty"
            return false
        } else if ((x.toFloat() < 1) or (x.toFloat() > 100)) {
            age.error = "Age should be between 1 and 100"
            return false
        } else {
            age.error = null
            age.isErrorEnabled = false
            return true
        }
    }

    private fun updateData() {

        if (!validateName() or !validateAge() or !validateHeight() or !validateWeight()) {
            return
        } else {
            if (isNameChanged() or isGenderChanged() or isHeightChanged() or isWeightChanged() or isAgeChanged()) {
                calculateBmi()
                calculateBmr()
                Toast.makeText(this, "Data has been updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "There is no change in data", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun isNameChanged(): Boolean {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users").child(UserId)

        val name = findViewById<TextInputLayout>(R.id.name)
        if (stringName != name.editText!!.text.toString()) {
            reference.child("name").setValue(name.editText!!.text.toString())
            stringName = name.editText!!.text.toString()
            return true
        } else {
            return false
        }
    }

    private fun isHeightChanged(): Boolean {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users").child(UserId)

        val height = findViewById<TextInputLayout>(R.id.height)
        if (stringHeight != height.editText!!.text.toString()) {
            reference.child("height").setValue(height.editText!!.text.toString())
            stringHeight = height.editText!!.text.toString()
            return true
        } else {
            return false
        }
    }

    private fun isWeightChanged(): Boolean {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users").child(UserId)

        val weight = findViewById<TextInputLayout>(R.id.weight)
        if (stringWeight != weight.editText!!.text.toString()) {
            reference.child("weight").setValue(weight.editText!!.text.toString())
            stringWeight = weight.editText!!.text.toString()
            return true
        } else {
            return false
        }
    }

    private fun isAgeChanged(): Boolean {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users").child(UserId)

        val age = findViewById<TextInputLayout>(R.id.age)
        if (stringAge != age.editText!!.text.toString()) {
            reference.child("age").setValue(age.editText!!.text.toString())
            stringAge = age.editText!!.text.toString()
            return true
        } else {
            return false
        }
    }

    private fun isGenderChanged(): Boolean {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users").child(UserId)

        val genderRg = findViewById<RadioGroup>(R.id.genderRg)
        val male = findViewById<RadioButton>(R.id.male)
        val female = findViewById<RadioButton>(R.id.female)

        val checkedString = when (genderRg.checkedRadioButtonId) {
            R.id.female -> "Female"
            else -> "Male"
        }

        if (stringGender != checkedString) {
            reference.child("gender").setValue(checkedString)
            stringGender = checkedString
            return true
        } else {
            return false
        }
    }

    private fun calculateBmi() {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users").child(UserId)

        var w = stringWeight.toFloat() //in  Kg
        var h = stringHeight.toFloat() / 100 //in m
        stringBmi =
            "%.2f".format(w / (h * h)).toString()
        reference.child("bmi").setValue(stringBmi)

    }

    private fun calculateBmr() {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users").child(UserId)

        var w = stringWeight.toFloat() //in  Kg
        var h = stringHeight.toFloat() //in Cm
        var a = stringAge.toFloat()  // in years

        if (stringGender == "Male") {
            stringBmr =

            "%.0f".format((88.362 + (13.397 * w) + (4.799 * h) - (5.677 * a)) * 1.375).toString()
            reference.child("bmr").setValue(stringBmr)
        } else {
            stringBmr =
                "%.0f".format((447.593 + (9.247 * w) + (3.098 * h) - (4.330 * a)) * 1.375).toString()
            reference.child("bmr").setValue(stringBmr)
        }
//        (66.47 + (13.75 * w) + (5.003 * h) - (6.755 * a)) * 1.55
//        (655.1 + (9.563 * w) + (1.85 * h) - (4.676 * a)) * 1.55
    }


    override fun onBackPressed() {
        if (!validateName() or !validateAge() or !validateHeight() or !validateWeight()) {
            return
        } else {
            startActivity(Intent(this, DashboardActivity::class.java))
            finishAffinity()
        }
    }
}

//
//BMR for Men = 66.47 + (13.75 * weight [kg]) + (5.003 * size [cm]) − (6.755 * age [years])
//BMR for Women = 655.1 + (9.563 * weight [kg]) + (1.85 * size [cm]) − (4.676 * age [years])

//avg calories = BMR * 1.55