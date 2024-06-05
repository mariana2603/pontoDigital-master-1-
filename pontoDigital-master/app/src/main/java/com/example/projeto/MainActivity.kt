package com.example.projeto

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.projeto.databinding.ActivityMainBinding

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
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Navigate to login fragment after initialization
        Handler(Looper.getMainLooper()).postDelayed({
            if (navController.currentDestination?.id == R.id.navigation_home) {
                navController.navigate(R.id.navigation_login)
            }
        }, 500) // delay of 500ms

        // Add OnDestinationChangedListener
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_login) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }
    }
}