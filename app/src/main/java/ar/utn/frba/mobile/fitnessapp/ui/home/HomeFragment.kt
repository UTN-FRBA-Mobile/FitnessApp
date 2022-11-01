package ar.utn.frba.mobile.fitnessapp.ui.home

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
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputEditText

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private var showBG: Boolean = true

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        view.findViewById<TextView>(R.id.textView).text = "Hola que tal"
    }

    override fun onStart() {
        super.onStart()
        showBG = MyPreferences.isShowBGsPreferredView(context!!)
        if(showBG){
            activity?.findViewById<ConstraintLayout>(R.id.homeScreen)?.setBackgroundResource(R.drawable.bg_yogax);
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}