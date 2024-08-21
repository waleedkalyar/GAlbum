package com.waleed.galbums.ui.activities.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.waleed.galbums.R
import com.waleed.galbums.databinding.ActivityMainBinding
import com.waleed.galbums.utils.extensions.checkMediaPermissions
import com.waleed.galbums.utils.extensions.requestMediaPermissions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        initActivity()
    }

    private fun initActivity() {
        initPermissions()
        initNavController()
        handleBackPress()
    }

    private fun initNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun initPermissions() {
        if (!checkMediaPermissions()) requestMediaPermissions {
            if (it)
                loadGallery()
            else {
                val snack = Snackbar.make(
                    this,
                    binding.root,
                    "Please Grant permissions to view Gallery",
                    Snackbar.LENGTH_SHORT
                )
                snack.setAction("Grant Permissions") {
                    initPermissions()
                }
                snack.show()
            }
        } else loadGallery()
    }

    private fun loadGallery() {
        viewModel.intiAlbums()
    }


    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback {
            if (navController.popBackStack().not()) {
                onBackPressedDispatcher.onBackPressed()
            } else navController.navigateUp()
        }
    }

}