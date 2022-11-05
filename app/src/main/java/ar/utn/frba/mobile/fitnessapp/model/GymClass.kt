package ar.utn.frba.mobile.fitnessapp.model

data class GymClass(
    val id: Int,
    val type: String,
    val startDate: String,
    val endDate: String,
    val professor: String,
    val people: Int,
    val maxCapacity: Int
)
