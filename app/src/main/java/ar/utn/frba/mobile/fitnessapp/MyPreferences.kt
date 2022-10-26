package ar.utn.frba.mobile.fitnessapp

import android.content.Context
import android.content.SharedPreferences


object MyPreferences {
    private const val showBGsKey = "preference_is_show_backgrounds"
    private val PREF_NAME = "PREF_NAME"
    private val FIREBASE_TOKEN = "FIREBASE_TOKEN"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getFirebaseToken(context: Context): String? {
        return getPreferences(context).getString(FIREBASE_TOKEN, null)
    }

    fun setFirebaseToken(context: Context, token: String) {
        val editor = getPreferencesEditor(context)
        editor.putString(FIREBASE_TOKEN, token)
        editor.apply()
    }

    private fun getPreferencesEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
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