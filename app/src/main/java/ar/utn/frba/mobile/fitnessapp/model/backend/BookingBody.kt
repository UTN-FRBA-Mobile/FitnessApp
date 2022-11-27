package ar.utn.frba.mobile.fitnessapp.model.backend

import com.google.gson.annotations.SerializedName

data class BookingBody(@SerializedName("user_id") val userId: String)
