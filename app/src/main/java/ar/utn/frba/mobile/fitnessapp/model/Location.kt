package ar.utn.frba.mobile.fitnessapp.model

import android.location.Location
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


abstract class Locatable {
    abstract fun androidLocation(): android.location.Location

    fun distance(other: Locatable): Float =
        androidLocation().distanceTo(other.androidLocation())
}

@Parcelize
data class Location (
    val latitude:  Double,
    val longitude: Double
) : Parcelable, Locatable() {
    override fun androidLocation(): android.location.Location {
        val location = android.location.Location("")
        location.apply {
            latitude = this.latitude
            longitude = this.longitude
        }
        return location
    }
}

// Extension method for android Location class
fun android.location.Location.asLocatable(): Locatable {
    return object:Locatable() {
        override fun androidLocation(): Location = this@asLocatable
    }
}