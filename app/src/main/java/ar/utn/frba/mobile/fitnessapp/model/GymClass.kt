package ar.utn.frba.mobile.fitnessapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

@Parcelize
data class GymClass(
    val id:          Int,
    @SerializedName("gym_id") val gymId:      Int,
    val type:        String,
    val startDate:   String,
    val endDate:     String,
    val professor:   String,
    val people:      Int,
    val maxCapacity: Int
) : Parcelable
