package com.example.esaitbiriyanicenter

import com.google.gson.annotations.SerializedName


data class Availability(
    @SerializedName("range")
    val range: String,
    @SerializedName("majorDimension")
    val majorDimension: String,
    @SerializedName("values")
    val values: ArrayList<Item>
)

data class Item(
    val bookName: String,
    val availability: String
)