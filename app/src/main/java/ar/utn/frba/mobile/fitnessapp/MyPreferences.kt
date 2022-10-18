package ar.utn.frba.mobile.fitnessapp

import android.content.Context
import android.content.SharedPreferences


object MyPreferences {
    private const val showBGsKey = "preference_is_show_backgrounds"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    fun setShowBGsPreferredView(context: Context, value: Boolean) {
        val edit: SharedPreferences.Editor = getPreferences(context).edit()
        edit.putBoolean(showBGsKey, value)
        edit.apply()
    }

    fun isShowBGsPreferredView(context: Context): Boolean {
        return getPreferences(context).getBoolean(showBGsKey, true)
    }
}