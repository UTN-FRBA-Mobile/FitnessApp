package ar.utn.frba.mobile.fitnessapp.ui.home

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.model.Gym
import ar.utn.frba.mobile.fitnessapp.model.asLocatable
import kotlin.math.floor

class GymSearchResultAdapter(private val currContext: Context, private val location: Location?, private val arrayList: ArrayList<Gym>)
    : ArrayAdapter<Gym>(currContext, R.layout.gym_result_item, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.gym_result_item, null)

        val gymAvatar: ImageView = view.findViewById(R.id.gymAvatar)
        val gymName: TextView = view.findViewById(R.id.gymName)
        val gymDistance: TextView = view.findViewById(R.id.gymDistance)

        val gym = arrayList[position]
        gymAvatar.setImageResource(randomAvatar())
        gymName.text = gym.name
        val distancePlaceholder = currContext.resources.getString(R.string.gymDistancePlaceholder)

        gymDistance.text = "???"
        if (location != null) {
            var distance = location.asLocatable().distance(gym.location)
            println(distance)
            var unitDesc = currContext.resources.getString(R.string.meterUnitDescription)
            if (distance > 1000) {
                unitDesc = currContext.resources.getString(R.string.kilometerUnitDescription)
                distance /= 1000
            }
            val distanceMsg = "$distancePlaceholder ${String.format("%.2f", distance)} $unitDesc"
            gymDistance.text = distanceMsg
        }

        return view
    }

    private fun randomAvatar(): Int {
        val availableIcons = arrayOf(
            R.drawable.gym_logo1,
            R.drawable.gym_logo2,
            R.drawable.gym_logo3,
            R.drawable.gym_logo4,
            R.drawable.gym_logo5
        )
        val index = floor(Math.random() * availableIcons.size).toInt()
        return availableIcons[index]
    }
}