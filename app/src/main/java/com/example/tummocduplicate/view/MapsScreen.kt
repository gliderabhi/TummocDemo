package com.example.tummocduplicate.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tummocduplicate.R
import com.example.tummocduplicate.bean.Route
import com.example.tummocduplicate.bean.RouteMediumEnum
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapsScreen(viewModel: ListOfRoutesViewModel, navHostController: NavHostController) {
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
                },
            navHostController,
            viewModel
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
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(count = routes.size, itemContent = {
            when (it) {
                0 -> SourceUI(routes[it])
                routes.size - 1 -> DestinationUI(routes[it])

            }
        })
    }
}

@Composable
fun SourceUI(route: Route) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(left = 20.dp, right = 20.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFf5f5f5))
                .absolutePadding(left = 20.dp, right = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {
            Column {
                Text(text = "Source", fontSize = 12.sp, color = Color.LightGray)
                Text(
                    text = route.sourceTitle,
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .absolutePadding(right = 50.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Source",
                    fontSize = 14.sp,
                    color = Color(0xFFFFAA05)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(color = Color.DarkGray, shape = RoundedCornerShape(30.dp))
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(30.dp)
                            )
                    ) {
                        Image(
                            painter = painterResource(
                                id = when (route.medium) {
                                    RouteMediumEnum.Bus.value -> R.drawable.ic_bus
                                    RouteMediumEnum.Walk.value -> R.drawable.ic_walk
                                    RouteMediumEnum.Train.value -> R.drawable.ic_train
                                    RouteMediumEnum.Metro.value -> R.drawable.ic_train
                                    else -> R.drawable.ic_bus
                                }
                            ),
                            contentDescription = "walk",
                            modifier = Modifier
                                .size(15.dp)
                                .align(Alignment.Center),
                            colorFilter = ColorFilter.tint(color = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_forward_24),
                        contentDescription = "forward icon",
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .align(
                        Alignment.TopEnd
                    )
                    .background(color = Color(0xFF708090), shape = RoundedCornerShape(30.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = "location",
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Center),
                    colorFilter = ColorFilter.tint(color = Color.White)
                )
            }
        }
    }
}

@Composable
fun DestinationUI(route: Route) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(left = 20.dp, right = 20.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFf5f5f5))
                .absolutePadding(left = 20.dp, right = 20.dp, top = 20.dp, bottom = 20.dp)
        ) {
            Text(text = "Destination", fontSize = 12.sp, color = Color.LightGray)
            Text(
                text = route.destinationTitle,
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(right = 50.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Source",
                fontSize = 14.sp,
                color = Color(0xFFFFAA05)
            )
        }
    }
}

@Composable
fun MapsComponent(
    modifier: Modifier,
    navHostController: NavHostController,
    viewModel: ListOfRoutesViewModel
) {
    viewModel.mapProperties = MapProperties(
        isBuildingEnabled = false,
//        isMyLocationEnabled = true,
//        isTrafficEnabled = true,
        mapType = MapType.NORMAL
    )

    val latLong =LatLng(
        viewModel.clickedRoute[0].sourceLat,
        viewModel.clickedRoute[0].sourceLong
    )
    Box(modifier) {
        GoogleMap(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            properties = viewModel.mapProperties,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            cameraPositionState = CameraPositionState(
                position = CameraPosition(
                    latLong,
                    15f,
                    0f,
                    0f
                )
            )
        )
        HeaderBackButton(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            navHostController
        )
    }
}

@Composable
private fun HeaderBackButton(
    modifier: Modifier,
    navHostController: NavHostController
) {
    Box(modifier = modifier) {
        Row(
            modifier = modifier
                .absolutePadding(left = 20.dp, top = 20.dp, right = 20.dp)
                .clickable { navHostController.navigate(Screens.Home.route) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row() {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { navHostController.navigate(Screens.Home.route) })
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Beta",
                    color = Color.Black,
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(5.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .background(
                    color = Color(0xFF708090),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(all = 5.dp)
                .align(Alignment.CenterEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = "exclamation",
                modifier = Modifier
                    .size(20.dp)
                    .clickable { navHostController.navigate(Screens.Home.route) })
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Report",
                color = Color.DarkGray
            )
        }
    }
}
