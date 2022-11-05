package ar.utn.frba.mobile.fitnessapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Gym (
    val avatar:  String,
    val name:    String,
    val address: String
) : Parcelable
