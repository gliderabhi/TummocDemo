package com.example.tummocduplicate.view

import android.content.Context
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
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import com.example.tummocduplicate.R
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel

@Composable
fun MainView(viewModel: ListOfRoutesViewModel) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ListOfRoutes(viewModel)
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