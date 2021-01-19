package com.example.caloriecounter.helperClass

class FoodItemHelperClass {

    var foodName: String? = null
    var foodCal: String? = null
    var foodQty: String? = null
    constructor() {

    }

    constructor(foodName: String?, foodCal: String?, foodQty: String?) {
        this.foodName = foodName
        this.foodCal = foodCal
        this.foodQty = foodQty
    }


}