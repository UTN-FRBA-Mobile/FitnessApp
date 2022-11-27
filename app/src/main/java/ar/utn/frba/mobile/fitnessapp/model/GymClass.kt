package ar.utn.frba.mobile.fitnessapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GymClass(
    val id:          String,
    @SerializedName("gym_id")
    val gymId:       String,
    val type:        String,
    val schedules:   List<Schedule>,
    val professor:   String,
    val people:      Int,
    @SerializedName("max_capacity")
    val maxCapacity: Int
) : Parcelable

@Parcelize
data class Schedule(
    @SerializedName("start_hour")
    val startHour: Double,
    @SerializedName("end_hour")
    val endHour:   Double,
    @SerializedName("week_day")
    val weekDay:   Int,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate:   String
) : Parcelable