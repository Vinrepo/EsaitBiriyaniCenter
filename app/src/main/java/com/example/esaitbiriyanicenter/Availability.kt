package com.example.esaitbiriyanicenter

import com.google.gson.annotations.SerializedName


data class Availability(
    @SerializedName("range")
    var range: String,
    @SerializedName("majorDimension")
    var majorDimension: String,
    @SerializedName("values")
    var values: ArrayList<Item>

)

data class Item(
    var bookName: String,
    var availability: String
)