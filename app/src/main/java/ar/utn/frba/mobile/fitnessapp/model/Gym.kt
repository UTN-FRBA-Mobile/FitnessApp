package ar.utn.frba.mobile.fitnessapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.http.GET


@Parcelize
data class Gym(
    val id: Int,
    val avatar: String,
    val name: String,
    val location: Location,
    val classes: ArrayList<GymClass>
) : Parcelable, Locatable() {
    override fun androidLocation(): android.location.Location = location.androidLocation()
}

interface GymsService {
    @GET("gyms")
    fun getGyms(): Call<List<Gym>>
}