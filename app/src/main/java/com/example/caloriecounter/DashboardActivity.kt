package com.example.caloriecounter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriecounter.adapter.FoodItemAdapter
import com.example.caloriecounter.helperClass.FoodItemHelperClass
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {
    val TAG = "DashboardActivity"

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var foodItemAdapter: FoodItemAdapter

    private lateinit var recyclerView: RecyclerView

    private var selectedDateFromCal: String? = null

    var date: String? = ""

    private var totalCal = 0f
    private var consumedCal = 0f

    var stringWeight: String = ""
    var stringHeight: String = ""
    var stringAge: String = ""
    var stringGender: String = ""
    var stringBmr: String = ""
    var stringBmrAsPerDate: String = ""
    var stringConsumedCalAsPerDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users").child(UserId)

        recyclerView = findViewById(R.id.recyclerView)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        //Intent to CalendarActivity
        val IvCalendar = findViewById<ImageView>(R.id.IvCalendar)
        IvCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        //Incoming Intent from CalendarActivity
        val incomingIntent = intent
        selectedDateFromCal = incomingIntent.getStringExtra("date")

        Log.d(TAG, "SELECTEDDATEFROMCAL$selectedDateFromCal")

        //Navigation View
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView.bringToFront()

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navHome -> drawerLayout.closeDrawer(GravityCompat.START)
                R.id.navProfile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finishAffinity()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.navLogout -> {
                    auth.signOut()
                    startActivity(Intent(this, SignInSignUpActivity::class.java))
                    finishAffinity()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
        navigationView.setCheckedItem(R.id.navHome)


        //setting date to today's date
        val simpleDateFormat = SimpleDateFormat("dd-M-yyyy")
        date = simpleDateFormat.format(Date())
        Log.d(TAG, "$date")

        val profilName = navigationView.getHeaderView(0).findViewById<TextView>(R.id.profileName)
        val profileEmail = navigationView.getHeaderView(0).findViewById<TextView>(R.id.profileEmail)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue().toString()
                val email = snapshot.child("email").getValue().toString()
                stringHeight = snapshot.child("height").getValue().toString()
                stringGender = snapshot.child("gender").getValue().toString()
                stringWeight = snapshot.child("weight").getValue().toString()
                stringAge = snapshot.child("age").getValue().toString()
                stringBmr = snapshot.child("bmr").getValue().toString()

                val tvConsumedCal = findViewById<TextView>(R.id.tvConsumedCal)
                stringConsumedCalAsPerDate =
                    snapshot.child("date").child("$date").child("consumedCal").getValue()
                        .toString()
                tvConsumedCal.setText("/$stringConsumedCalAsPerDate")
                profilName.text = name
                profileEmail.text = email

                getBmr()

                if ((selectedDateFromCal == null) or (selectedDateFromCal == "") or (selectedDateFromCal == date)) {
                    stringBmrAsPerDate = stringBmr
                    stringConsumedCalAsPerDate =
                        snapshot.child("date").child("$date").child("consumedCal").getValue()
                            .toString()
                    Log.d(TAG, " inside if for selectedDateforCal is null $date")
                } else {
                    if (snapshot.child("date").hasChild(selectedDateFromCal!!)) {
                        stringBmrAsPerDate =
                            snapshot.child("date").child("$selectedDateFromCal").child("bmr")
                                .getValue()
                                .toString()
                        stringConsumedCalAsPerDate =
                            snapshot.child("date").child("$selectedDateFromCal")
                                .child("consumedCal")
                                .getValue()
                                .toString()
                    } else {
                        stringBmrAsPerDate = "0"
                        stringConsumedCalAsPerDate = "0"
                    }
                }

                totalCal = stringBmrAsPerDate.toFloatOrNull()!!
                Log.d(TAG, "totalCal is set $totalCal")
                if (stringConsumedCalAsPerDate.toFloatOrNull() == null) {
                    consumedCal = 0f
                    reference.child("date").child("$date").child("consumedCal").setValue("0")
                    Log.d(TAG, "consumedCal to 0 $consumedCal")
                } else {
                    consumedCal = stringConsumedCalAsPerDate.toFloatOrNull()!!
                    Log.d(TAG, "consumedCal $consumedCal")
                }
                setCalories()

                //Firebase RecyclerView
                foodItemRecycler()
                foodItemAdapter.startListening()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled")
            }
        })

        //FloatingActionButton
        val addFloatingButton = findViewById<FloatingActionButton>(R.id.addFloatingButton)
        addFloatingButton.setOnClickListener {
            val i = Intent(this, addFoodItemActivity::class.java)
            startActivity(i)
        }
    }


    private fun setCalories() {
        val circularProgressBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)
        Log.d(TAG, "Inside SetCalories circularProgressBar")
        circularProgressBar.apply {
            setProgressWithAnimation(consumedCal, 1000)
            progressMax = totalCal
        }
        val tvConsumedCal = findViewById<TextView>(R.id.tvConsumedCal)
        val tvTotalCal = findViewById<TextView>(R.id.tvTotalCal)
        tvConsumedCal.text = (consumedCal.toInt()).toString()
        tvTotalCal.text = "/${totalCal.toInt()}"
        Log.d(TAG, "Inside SetCalories")
    }


//    private fun ifProfileInComplete() {
//        if ((stringHeight == "") or (stringWeight == "") or (stringAge == "")) {
//            startActivity(Intent(this, ProfileActivity::class.java))
//            finishAffinity()
//        }
//    }


    private fun getBmr() {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid
        database = FirebaseDatabase.getInstance()
        val ref = database.getReference("users").child(UserId)

        ref.child("date").child("$date").child("bmr").setValue(stringBmr)
    }


    //Firebase RecyclerView
    private fun foodItemRecycler() {
        Log.d(TAG, "Inside foodItemRecycler()")
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid
        database = FirebaseDatabase.getInstance()

        val ref: DatabaseReference

        if ((selectedDateFromCal == null) or (selectedDateFromCal == "") or (selectedDateFromCal == date)) {
            ref = database.getReference("users").child(UserId).child("date").child("$date")
                .child("foodItems")
        } else {
            ref = database.getReference("users").child(UserId).child("date")
                .child("$selectedDateFromCal")
                .child("foodItems")
        }


        recyclerView.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )

        val Foptions: FirebaseRecyclerOptions<FoodItemHelperClass> =
            FirebaseRecyclerOptions.Builder<FoodItemHelperClass>()
                .setQuery(
                    ref, FoodItemHelperClass::class.java
                ).build()

        if ((selectedDateFromCal == null) or (selectedDateFromCal == "") or (selectedDateFromCal == date)) {
            foodItemAdapter = FoodItemAdapter(Foptions, date, this)
        } else {
            foodItemAdapter = FoodItemAdapter(Foptions, selectedDateFromCal, this)
        }
        recyclerView.adapter = foodItemAdapter
    }

    //to clear the focus if onBackPressed to current activity
    override fun onResume() {
        super.onResume()
        navigationView.setCheckedItem(R.id.navHome)
    }


    private var back_pressed_time: Long = 0

    //Overriding onBackPressed to Open and Close Drawer
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            //Exit if back button is clicked twice
            if (back_pressed_time + 2000 > System.currentTimeMillis()) {

            } else Toast.makeText(baseContext, "Press once again to exit!", Toast.LENGTH_SHORT)
                .show()
            back_pressed_time = System.currentTimeMillis()
        }
    }

}
