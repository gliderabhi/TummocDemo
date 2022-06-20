package com.example.tummocduplicate.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.rememberScrollState
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.tummocduplicate.ui.theme.TummocDuplicateTheme
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ListOfRoutesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getPermission()
        viewModel = ViewModelProvider(this).get(ListOfRoutesViewModel::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getDataFromFirebase()
            viewModel.setupZoomValues()
        }
        setContent {
            val v = rememberScrollState()
            TummocDuplicateTheme {
                viewModel.navController = rememberNavController()
                SetupNavGraph(viewModel.navController, viewModel, applicationContext)

            }
        }
    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.permissionGranted.value = true
//            getLocationCurrent()
        } else {
//            ContextCompat.sh
        }
    }

//    private fun getLocationCurrent() {
//        val fusedLocation = LocationServices.getFused
//    }

}
