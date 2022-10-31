package ar.utn.frba.mobile.fitnessapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentMapBinding
import com.google.android.gms.maps.SupportMapFragment

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private var showBG: Boolean = true

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        val mapFragment = SupportMapFragment.newInstance()
        _binding
            .beginTransaction()
            .add(R.id.my_container, mapFragment)
            .commit()
//        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textMap
//        mapViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onStart() {
        super.onStart()
        showBG = MyPreferences.isShowBGsPreferredView(context!!)
        if(showBG){
            activity?.findViewById<ConstraintLayout>(R.id.mapScreen)?.setBackgroundResource(R.drawable.bg_proteinx);
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}