package com.example.tummocduplicate.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel


@Composable
fun MapsScreen(viewModel: ListOfRoutesViewModel) {
    val mapsHeight = remember {
        mutableStateOf(0.5f)
    }
    val mapHeight = animateFloatAsState(targetValue = mapsHeight.value)
    Column(modifier = Modifier.fillMaxSize()) {
        MapsComponent(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(mapHeight.value)
                .clickable {
                    mapsHeight.value = 0.7f
                }
        )
        PathSequence(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f)
                .clickable {
                    mapsHeight.value = 0.3f
                },
            viewModel
        )

    }
}

@Composable
fun PathSequence(modifier: Modifier, viewModel: ListOfRoutesViewModel) {
    val routes = viewModel.clickedRoute
    LazyColumn(modifier = modifier) {
        items(count = routes.size, itemContent = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Column() {
                        
                    }
                }
            }
        })
    }
}

@Composable
fun MapsComponent(modifier: Modifier) {
//    GoogleMap(modifier = modifier)
    Column(modifier) {

    }
}
