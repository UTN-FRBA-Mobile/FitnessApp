package ar.utn.frba.mobile.fitnessapp.model.backend

import ar.utn.frba.mobile.fitnessapp.model.Location
import com.google.gson.annotations.SerializedName

data class GymQuery(
    val name:         String,
    @SerializedName("near_point")
    val nearPoint:    Location?,
    @SerializedName("search_radius")
    val searchRadius: Double
)
