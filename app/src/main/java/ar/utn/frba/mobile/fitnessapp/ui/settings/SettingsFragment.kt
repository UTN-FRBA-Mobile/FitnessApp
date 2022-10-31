package ar.utn.frba.mobile.fitnessapp.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import ar.utn.frba.mobile.fitnessapp.MainActivity
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private var showBG: Boolean = true

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSettings
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        //Toast.makeText(activity, "Hola settings", Toast.LENGTH_SHORT).show()
        val settingsScreen = activity?.findViewById<ScrollView>(R.id.settingsScreen)
        val chkBG  = activity?.findViewById<CheckBox>(R.id.check_bg);
        showBG = MyPreferences.isShowBGsPreferredView(context!!)

        chkBG?.setChecked(showBG)
        if(showBG){
            settingsScreen?.setBackgroundResource(R.drawable.bg_beachx);
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Toast.makeText(activity, "Chau settings", Toast.LENGTH_SHORT).show()
        _binding = null
    }

}