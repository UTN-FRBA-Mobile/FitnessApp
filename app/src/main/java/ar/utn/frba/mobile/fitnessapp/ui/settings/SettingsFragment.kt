package ar.utn.frba.mobile.fitnessapp.ui.settings

import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import ar.utn.frba.mobile.fitnessapp.MainActivity
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private var showBG: Boolean = true
    private var showCamInfo: Boolean = false
    private var cameraID: Int = 0

    private var cameraProvider: ProcessCameraProvider? = null

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

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.spinner.setOnSpinnerItemSelected(){
            closeCamera()
            MediaPlayer.create(activity, R.raw.camera4).start()
        }
    }*/

    override fun onStart() {
        super.onStart()
        //Toast.makeText(activity, "Hola settings", Toast.LENGTH_SHORT).show()
        val settingsScreen = activity?.findViewById<ScrollView>(R.id.settingsScreen)

        val chkBG  = activity?.findViewById<CheckBox>(R.id.check_bg)
        showBG = MyPreferences.isShowBGsPreferredView(context!!)
        chkBG?.setChecked(showBG)
        if(showBG){
            settingsScreen?.setBackgroundResource(R.drawable.bg_beachx);
        }
/*
        val chkCamInfo  = activity?.findViewById<CheckBox>(R.id.camera_info)
        showCamInfo = MyPreferences.isCamInfoEnabled(context!!)
        chkCamInfo?.setChecked(showCamInfo)

        cameraProvider = ProcessCameraProvider.getInstance(requireContext()).get()
        setDropdown()
        */

    }

    private fun setDropdown() {
        val size: Int = cameraProvider!!.availableCameraInfos.size

        val dropdown: Spinner? = activity?.findViewById(R.id.spinner)
        val items = Array(size){"$it"}
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown?.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Toast.makeText(activity, "Chau settings", Toast.LENGTH_SHORT).show()
        _binding = null
    }

}