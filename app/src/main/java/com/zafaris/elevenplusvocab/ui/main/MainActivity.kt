package com.zafaris.elevenplusvocab.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.databinding.ActivityMainBinding
import com.zafaris.elevenplusvocab.ui.settings.SettingsActivity
import java.util.*

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_share -> {
                val sendIntent = Intent(Intent.ACTION_SEND)
                val appPackageName = packageName //TODO: getPackageName();
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Download the 11+ Learn Vocab app at https://play.google.com/store/apps/details?id=$appPackageName")
                sendIntent.type = "text/plain"

                // intent to Share
                val shareIntent = Intent.createChooser(sendIntent, "Share using")
                startActivity(shareIntent)

                true
            }
            R.id.menu_rate -> {
                val appPackageName = packageName

                // intent to Google Play Store
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
                    startActivity(intent)
                }

                true
            }
            R.id.menu_settings -> {
                // intent to Settings Activity
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}