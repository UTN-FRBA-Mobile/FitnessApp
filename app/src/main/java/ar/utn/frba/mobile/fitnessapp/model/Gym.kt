package ar.utn.frba.mobile.fitnessapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.http.GET
import com.google.gson.annotations.SerializedName


@Parcelize
data class Gym(
    val id:       String,
    val image:    String?,
    val name:     String,
    @SerializedName("contact_info")
    val contactInfo: String,
    val description: String,
    val location: Location,
    val classes:  ArrayList<GymClass>
) : Parcelable, Locatable() {
    override fun androidLocation(): android.location.Location = location.androidLocation()
}