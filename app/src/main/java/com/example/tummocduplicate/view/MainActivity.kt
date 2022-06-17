package com.example.tummocduplicate.view

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.tummocduplicate.R
import com.example.tummocduplicate.ui.theme.TummocDuplicateTheme
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ListOfRoutesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ListOfRoutesViewModel::class.java)
        initializeDatabase()
//        viewModel.getTummocDataFromFile(applicationContext)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getDataFromFirebase()
        }
        setContent {
            TummocDuplicateTheme {
                // A surface container using the 'background' color from the theme
                MainView(viewModel, applicationContext)
            }
        }
    }

    private fun initializeDatabase() {
//        viewModel.database =
//            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "TummocDb").build()
    }

    @Composable
    fun MainView(viewModel: ListOfRoutesViewModel, applicationContext: Context) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ListOfRoutes(viewModel, applicationContext)
            FloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .absolutePadding(right = 20.dp, bottom = 30.dp)
                    .size(30.dp),
                contentColor = Color.White,
                backgroundColor = Color(0xFFFFA005),
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_filter_24),
                        contentDescription = "filter"
                    )
                }
            )
        }
    }
}
