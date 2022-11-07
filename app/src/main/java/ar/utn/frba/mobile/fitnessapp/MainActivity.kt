package ar.utn.frba.mobile.fitnessapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ar.utn.frba.mobile.fitnessapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_calendar,
                R.id.navigation_qr,
                R.id.navigation_map,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setBottomNavVisibility()

        setFirebaseTokenInView()
        binding.reloadButton.setOnClickListener { setFirebaseTokenInView() }

//        binding.copyButton.setOnClickListener { copyTokenToClipboard() }

        binding.subscribeButton.setOnClickListener { subscribeToTopic() }
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

    private fun setFirebaseTokenInView() {
        val firebaseTokenText = MyPreferences.getFirebaseToken(this)

        if (binding.firebaseToken.text != null) {
            binding.firebaseToken.text = firebaseTokenText
            binding.reloadButton.visibility = View.GONE
//            binding.copyButton.visibility = View.VISIBLE
            binding.topicContainer.visibility = View.VISIBLE
        } else {
//            binding.copyButton.visibility = View.GONE
            binding.topicContainer.visibility = View.GONE
            binding.reloadButton.visibility = View.VISIBLE
        }
    }

    private fun subscribeToTopic() {
        val topicText = binding.topic.text.toString()
        FirebaseMessaging.getInstance().subscribeToTopic(topicText)
        Toast.makeText(this, "Subscripto a $topicText", Toast.LENGTH_SHORT).show()
        binding.topic.setText("")
    }

    fun copyTokenToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Firebase token", binding.firebaseToken.text)
        clipboard.setPrimaryClip(clip)
    }

    private fun setBottomNavVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            showBottomNav()

            val subFragments = setOf(
                R.id.classesFragment,
                R.id.navigation_details
            )

            if (destination.id in subFragments) {
                hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        binding.navView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.navView.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}