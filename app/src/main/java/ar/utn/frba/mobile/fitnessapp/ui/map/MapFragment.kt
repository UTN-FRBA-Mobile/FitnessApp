package ar.utn.frba.mobile.fitnessapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Buenos Aires
        val sydney = LatLng(-34.6, -58.3)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Buenos Aires"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}