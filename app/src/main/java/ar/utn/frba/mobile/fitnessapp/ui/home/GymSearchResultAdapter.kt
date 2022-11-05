package ar.utn.frba.mobile.fitnessapp.ui.home

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.model.Gym

class GymSearchResultAdapter(private val currContext: Context, private val arrayList: ArrayList<Gym>)
    : ArrayAdapter<Gym>(currContext, R.layout.gym_result_item, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.gym_result_item, null)

        val gymAvatar: ImageView = view.findViewById(R.id.gymAvatar)
        val gymName: TextView = view.findViewById(R.id.gymName)
        val gymAddress: TextView = view.findViewById(R.id.gymAddress)

        val gym = arrayList[position]
        gymAvatar.setImageURI(Uri.parse(gym.avatar))
        gymName.text = gym.name
        gymAddress.text = gym.address

        return view
    }
}