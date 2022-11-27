package ar.utn.frba.mobile.fitnessapp.ui.home

import android.Manifest
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.Permissions
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentHomeBinding
import ar.utn.frba.mobile.fitnessapp.model.Gym
import ar.utn.frba.mobile.fitnessapp.model.toLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkLocationPermissions()) {
            onLocationFound(this::setupWithLocation)
        } else {
            setupWithLocation(null)
        }

        val searchbar = binding.searchbar
        val searchButton = binding.searchButton

        // Enter Key in searchbar raises the searchButton's onClick event.
        searchbar.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(v.isFocused && keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                searchButton.performClick()
                return@OnKeyListener true
            }

            false
        })
    }

    override fun onStart() {
        super.onStart()
        val showBG = MyPreferences.isShowBGsPreferredView(requireContext())
        if(showBG){
            activity?.findViewById<ConstraintLayout>(R.id.homeScreen)?.setBackgroundResource(R.drawable.bg_yogax)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onLocationFound(callback: (Location?) -> Unit) {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener(callback)
    }

    private fun setupWithLocation(location: Location?) {
        val searchbar = binding.searchbar
        val searchButton = binding.searchButton

        searchButton.setOnClickListener {
            hideKeyBoard(it)
            val query: String = searchbar.text.toString()
            search(query, location)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { arrayList ->
            val adapter = GymSearchResultAdapter(requireContext(), location, arrayList)
            val resultList = binding.resultList
            resultList.adapter = adapter

            resultList.setOnItemClickListener { parent, _, position, _ ->
                val gym: Gym = parent.getItemAtPosition(position) as Gym
                val action = HomeFragmentDirections.actionNavigationHomeToDetailsFragment(gym)
                navController.navigate(action)
            }
        }

        search(location = location)
    }

    private fun hideKeyBoard(view: View) {
        val inputManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun checkLocationPermissions(): Boolean {
        return Permissions.checkForPermissions(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            100,
            "Fitness App necesita poder acceder a tu ubicacion para poder arrojarte mejores resultados de búsqueda."
        )
    }

    private fun search(query: String = "", location: Location? = null) {
        viewModel.search(query, location?.toLocation()) { _, t ->
            Toast.makeText(requireContext(), "An error ocurred when attempting to comunicate with the server", Toast.LENGTH_LONG).show()
            Log.println(Log.WARN, "[HOME_FRAGMENT][SEARCH]", t.toString())
        }
    }
}