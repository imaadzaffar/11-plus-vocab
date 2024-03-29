package com.zafaris.learnvocab.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.qonversion.android.sdk.Qonversion
import com.zafaris.learnvocab.R
import com.zafaris.learnvocab.databinding.ActivityMainBinding
import com.zafaris.learnvocab.util.API_KEY

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupToolbar()
        setupBottomNavigation()
        visibilityNavElements()

        /* Revenue Cat
        Purchases.debugLogsEnabled = true
        Purchases.configure(this, API_KEY, null, false)
        */

        Qonversion.setDebugMode()
        Qonversion.launch(application, API_KEY, false)
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbar.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf (
                R.id.homeFragment,
                R.id.wordsListFragment,
                R.id.guideFragment
            )
        )
        binding.toolbar.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun visibilityNavElements() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.wordsListFragment,
                R.id.guideFragment -> binding.bottomNavigation.visibility = View.VISIBLE
                else -> binding.bottomNavigation.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}