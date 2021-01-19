package com.example.caloriecounter.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.caloriecounter.R
import com.example.caloriecounter.helperClass.FoodItemHelperClass
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class FoodItemAdapter(
    foptions: FirebaseRecyclerOptions<FoodItemHelperClass>,
    val date: String?,
    val context: Context
) : FirebaseRecyclerAdapter<FoodItemHelperClass, FoodItemAdapter.FoodItemViewHolder>(foptions) {

    val TAG = "FoodItemAdapter"
    var TotalCal = 0
    var stringTotalCal: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {

        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.food_item_card, parent, false)
        return FoodItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(
        holder: FoodItemViewHolder,
        position: Int,
        model: FoodItemHelperClass
    ) {
        val auth: FirebaseAuth
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val UserId = user!!.uid
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users").child(UserId).child("date").child("$date")
        Log.d(TAG, "Inside OnBindViewHolder$date")

        val tCal: Int = model.foodQty!!.toInt() * model.foodCal!!.toInt()
        holder.foodName.text = model.foodName
        holder.foodQty.text = "Qty : ${model.foodQty}"
        holder.foodCal.text = "Total Calories : ${tCal}"

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                stringTotalCal = snapshot.child("consumedCal").getValue().toString()
                TotalCal = stringTotalCal!!.toInt()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled for loading TotalCal")
            }
        })

        val simpleDateFormat = SimpleDateFormat("dd-M-yyyy")
        val d = simpleDateFormat.format(Date())
        Log.d(TAG, "Inside OnBindViewHolder Before OnCLickListener $d")


        holder.removeFood.setOnClickListener {
            if (d == date) {
                val key = getRef(holder.adapterPosition).key //gets the key of the item clicked
                Log.d(TAG, "Inside OnClickListener of removeFood $key")
                TotalCal = TotalCal - tCal
                stringTotalCal = TotalCal.toString()

                reference.child("consumedCal").setValue(stringTotalCal)
                reference.child("foodItems").child(key!!)
                    .removeValue() //deletes item from database and view
            } else {
                Toast.makeText(context, "Old data cannot be modified", Toast.LENGTH_SHORT).show()
            }
        }

    }

    class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val removeFood: ImageView = itemView.findViewById(R.id.removeFood)
        val foodName: TextView = itemView.findViewById(R.id.foodName)
        val foodQty: TextView = itemView.findViewById(R.id.foodQty)
        val foodCal: TextView = itemView.findViewById(R.id.foodCal)

    }

}
