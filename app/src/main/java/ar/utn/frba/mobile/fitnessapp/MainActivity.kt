package ar.utn.frba.mobile.fitnessapp

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ar.utn.frba.mobile.fitnessapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_calendar, R.id.navigation_qr, R.id.navigation_map, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.check_bg -> {
                    val settingsScreen = findViewById<ConstraintLayout>(R.id.settingsScreen)
                    if (checked) {
                        MyPreferences.setShowBGsPreferredView(this, true)
                        settingsScreen?.setBackgroundResource(R.drawable.bg_beachx);
                    } else {
                        MyPreferences.setShowBGsPreferredView(this, false)
                        settingsScreen?.setBackgroundResource(0);
                    }
                }
                R.id.adorno -> {
                    if (checked) {
                        Toast.makeText(this, getString(R.string.hola), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, getString(R.string.chau), Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    fun sendMessage(view: View) {
        // Do something in response to button click
        Toast.makeText(this, getString(R.string.test), Toast.LENGTH_SHORT).show()
        //settingsFragment?.setBackgroundColor(Color.WHITE)
    }

}