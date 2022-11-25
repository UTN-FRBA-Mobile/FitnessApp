package ar.utn.frba.mobile.fitnessapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentMapBinding
import ar.utn.frba.mobile.fitnessapp.model.Gym
import ar.utn.frba.mobile.fitnessapp.model.GymsService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private var showBG: Boolean = true
    private lateinit var mMap: GoogleMap


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        val mapFragment = activity?.supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//        activity?.supportFragmentManager?.findFragmentById(R.id.map)

        return root
    }

    override fun onStart() {
        super.onStart()
        val service = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()) // Para parsear automágicamente el json
            .baseUrl("https://utn-fitness-api.fly.dev/api/v1/")
            .build()
            .create(GymsService::class.java) // la interfaz que diseñaron antes

        service.getGyms().enqueue(object : Callback<List<Gym>> {
            override fun onResponse(call: Call<List<Gym>>, response: Response<List<Gym>>) {

//                tweetsAdapter = TweetsAdapter(listener, response.body()!!)
                val gyms = response.body()!!
                gyms.forEach { addMarker(it) }
//                binding.list.apply {
//                    layoutManager = LinearLayoutManager(context)
//                    adapter = tweetsAdapter
//                }
            }

            override fun onFailure(call: Call<List<Gym>>, t: Throwable) {
                Toast.makeText(activity, "No Gyms founds!", Toast.LENGTH_SHORT).show()
            }
        })
        showBG = MyPreferences.isShowBGsPreferredView(context!!)
        if (showBG) {
            activity?.findViewById<ConstraintLayout>(R.id.mapScreen)
                ?.setBackgroundResource(R.drawable.bg_proteinx)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun addMarker(gym: Gym) {
        val position = LatLng(gym.location.latitude, gym.location.longitude)
        mMap.addMarker(MarkerOptions().position(position).title(gym.name))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Buenos Aires
        val sydney = LatLng(-34.6, -58.3)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Buenos Aires"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}