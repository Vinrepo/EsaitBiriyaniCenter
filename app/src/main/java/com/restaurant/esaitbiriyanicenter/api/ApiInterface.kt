package com.restaurant.esaitbiriyanicenter.api
import com.example.esaitbiriyanicenter.Availability
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("1QCFhzIprpRtxev7vqX9lceyFI8-u7kdp0wvKG5UygpU/values/Sheet1!A2:B6?key=AIzaSyDoN_X53uKx4_-QTVDQyC7_9pL3kRlZdlg")
    fun getBooks() : Call<Availability>
}