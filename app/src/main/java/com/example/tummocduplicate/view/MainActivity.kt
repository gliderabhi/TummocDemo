package com.example.tummocduplicate.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.tummocduplicate.ui.theme.TummocDuplicateTheme
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.jar.Manifest

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ListOfRoutesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ListOfRoutesViewModel::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getDataFromFirebase()
            viewModel.setupZoomValues()
        }
        setContent {
            TummocDuplicateTheme {
                viewModel.navController = rememberNavController()
                SetupNavGraph(viewModel.navController, viewModel, applicationContext)
            }
        }
    }

}
