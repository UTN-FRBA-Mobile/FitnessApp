package ar.utn.frba.mobile.fitnessapp.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.model.Gym
import ar.utn.frba.mobile.fitnessapp.model.asLocatable
import ar.utn.frba.mobile.fitnessapp.model.backend.BackendService
import ar.utn.frba.mobile.fitnessapp.model.backend.call
import com.squareup.picasso.Picasso
import kotlin.math.floor

class GymSearchResultAdapter(private val currContext: Context, private val location: Location?, private val arrayList: ArrayList<Gym>)
    : ArrayAdapter<Gym>(currContext, R.layout.gym_result_item, arrayList) {

    private val backend: BackendService = BackendService.create()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.gym_result_item, null)

        val gymAvatar: ImageView = view.findViewById(R.id.gymAvatar)
        val gymName: TextView = view.findViewById(R.id.gymName)
        val gymDistance: TextView = view.findViewById(R.id.gymDistance)

        val gym = arrayList[position]

        gymAvatar.setImageResource(R.drawable.logo)
        backend.image(gym.id).call(
            onResponse = { _, res ->
                val base64Img: String = res.body()!!
                val decodedImage: ByteArray = Base64.decode(base64Img, Base64.DEFAULT)
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
                gymAvatar.setImageBitmap(bitmap)
            }
        )

        gymName.text = gym.name
        val distancePlaceholder = currContext.resources.getString(R.string.gymDistancePlaceholder)

        gymDistance.text = "Not available"
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
}