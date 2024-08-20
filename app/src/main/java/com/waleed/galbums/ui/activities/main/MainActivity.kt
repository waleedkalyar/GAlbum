package com.waleed.galbums.ui.activities.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
}