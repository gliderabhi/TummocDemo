package com.example.tummocduplicate.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.tummocduplicate.R
import com.example.tummocduplicate.bean.Route
import com.example.tummocduplicate.bean.RouteMediumEnum
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel
import com.google.android.libraries.maps.model.BitmapDescriptor
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import java.text.DecimalFormat
import kotlin.math.roundToInt


@Composable
fun MapsScreen(viewModel: ListOfRoutesViewModel, navHostController: NavHostController) {

    val mapHeight = animateFloatAsState(targetValue = viewModel.mapsHeight.value)
    val routes = viewModel.clickedRoute
    setupCameraPositionAndZoom(viewModel, routes[0])
    Column(modifier = Modifier.fillMaxSize()) {
        MapsComponent(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(mapHeight.value),
            navHostController,
            viewModel = viewModel
        )
        PathSequence(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f)
                .clickable {
                    viewModel.mapsHeight.value = 0.3f
                },
            viewModel,
            routes
        )

    }
}

@Composable
fun PathSequence(modifier: Modifier, viewModel: ListOfRoutesViewModel, routes: List<Route>) {

    LazyColumn(
        modifier = modifier.absolutePadding(bottom = 10.dp, top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SourceUI(route = routes[0], viewModel)
        }
        items(count = routes.size, itemContent = {
            PickUpDropPointUi(route = routes[it], viewModel)
        }
        )
        item {
            DestinationUI(route = routes[routes.size - 1], viewModel)
        }
    }
}

@Composable
fun PickUpDropPointUi(route: Route, viewModel: ListOfRoutesViewModel) {
    val size = remember { mutableStateOf(IntSize.Zero) }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .then(
                    with(LocalDensity.current) {
                        Modifier.size(
                            width = 70.dp,
                            height = ((size.value.height).toDp().value).dp,
                        )
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .height(20.dp)
                    .width(2.dp)
                    .background(color = getRouteColor(route))
                    .align(Alignment.TopCenter)
            ) {}

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .absolutePadding(top = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = getRouteDrawable(route.medium)),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = getRouteColor(route)),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Row(
                modifier = Modifier
                    .then(
                        with(LocalDensity.current) {
                            Modifier.size(
                                width = 2.dp,
                                height = ((size.value.height).toDp().value - 90).dp,
                            )
                        }
                    )
                    .background(color = getRouteColor(route))
                    .align(Alignment.Center)
            ) {}

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .absolutePadding(bottom = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = getRouteDrawable(route.medium)),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = getRouteColor(route)),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Row(
                modifier = Modifier
                    .height(20.dp)
                    .width(2.dp)
                    .background(color = getRouteColor(route))
                    .align(Alignment.BottomCenter)
            ) {}

        }
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
                .absolutePadding(right = 10.dp)
                .onSizeChanged {
                    size.value = it
                },
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0XFFE8F0F0))
                    .clickable {
                        setupCameraPositionAndZoom(viewModel, route)
                        viewModel.mapsHeight.value = 0.5f
                    }
                    .absolutePadding(left = 20.dp, top = 10.dp, bottom = 20.dp)

            ) {
                Text(text = "Get in station", fontSize = 10.sp, color = Color.LightGray)
                Text(
                    text = route.sourceTitle,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                )
//            Spacer(modifier = Modifier.height(5.dp))

                BusTrainPathTimeDurationInfoUI(route)

//            Spacer(modifier = Modifier.height(5.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "NEXT SCHEDULE :", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = "${getTime(route.sourceTime)} pm", fontSize = 10.sp)

                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = "${getTime(route.destinationTime)} pm",
                        modifier = Modifier
                            .background(color = Color(0xFF0000F0))
                            .padding(5.dp),
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }

//            Spacer(modifier = Modifier.height(5.dp))

                Text(text = "Get down station", fontSize = 10.sp, color = Color.LightGray)
                Text(
                    text = route.destinationTitle,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun BusTrainPathTimeDurationInfoUI(route: Route) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(25.dp)
                .background(
                    color = Color(0xFF708090),
                    shape = RoundedCornerShape(25.dp)
                )
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
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
            modifier = Modifier.size(10.dp)
        )

        if (route.medium != RouteMediumEnum.Walk.value) {
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = route.busRouteDetails?.routeNumber.toString(),
                color = Color(0xFFFFAA05),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(5.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_forward_24),
                contentDescription = "forward icon",
                modifier = Modifier.size(10.dp)
            )
        }

        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "${roundTheNumber(route.distance)} km", fontSize = 12.sp)

        Spacer(modifier = Modifier.width(5.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_forward_24),
            contentDescription = "forward icon",
            modifier = Modifier.size(10.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "~ ${getTime(route.duration)} ,\u20b9 ${route.fare}", fontSize = 12.sp)


    }
}

fun getTime(time: String): String {
    var finalString = ""
    try {
        val times = time.split(":")
        val day = times[0].toInt()
        val hr = times[1].toInt()
        val min = times[2].toInt()

        if (day != 0) finalString += "$day hrs "
        if (hr != 0) finalString += "$hr min "
        if (min != 0) finalString += "$min sec"
    } catch (e: Exception) {
        e.printStackTrace()
        finalString = time
    }
    return finalString
}

fun roundTheNumber(numInDouble: Double): String {
    return DecimalFormat("#,##0.00").format(numInDouble)
}

@Composable
fun SourceUI(route: Route, viewModel: ListOfRoutesViewModel) {
    val size = remember { mutableStateOf(IntSize.Zero) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .then(
                    with(LocalDensity.current) {
                        Modifier.size(
                            width = 70.dp,
                            height = ((size.value.height).toDp().value).dp,
                        )
                    }
                )
        ) {
            Row(modifier = Modifier
                .align(Alignment.TopCenter)
                .absolutePadding(top = 20.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_source_desti),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = getRouteColor(route))
                )
            }
            Row(
                modifier = Modifier
                    .then(
                        with(LocalDensity.current) {
                            Modifier.size(
                                width = 2.dp,
                                height = ((size.value.height).toDp().value - 30).dp,
                            )
                        }
                    )
                    .background(color = getRouteColor(route))
                    .align(Alignment.BottomCenter)
            ) {}

        }
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
                .absolutePadding(right = 10.dp)
                .onSizeChanged {
                    size.value = it
                },
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0XFFE8F0F0))
                    .clickable {
                        setupCameraPositionAndZoom(viewModel, route)
                        viewModel.mapsHeight.value = 0.5f
                    }
                    .absolutePadding(left = 20.dp, right = 10.dp, top = 20.dp, bottom = 20.dp)

            ) {
                Column {
                    Text(text = "Source", fontSize = 12.sp, color = Color.LightGray)
                    Text(
                        text = route.sourceTitle,
                        fontSize = 14.sp,
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
                        fontSize = 12.sp,
                        color = Color(0xFFFFAA05)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BusTrainPathTimeDurationInfoUI(route = route)
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
}

fun setupCameraPositionAndZoom(
    viewModel: ListOfRoutesViewModel,
    route: Route
) {
    viewModel.cameraPositionLatLong.value = LatLng(
        route.sourceLat,
        route.sourceLong
    )
    for (i in viewModel.zoomValuesList.iterator()) {
        if (i.value < route.distance * 1000) {
            viewModel.zoomValues.value = (i.key - 1).toFloat()
            break
        }
    }
}

@Composable
fun DestinationUI(route: Route, viewModel: ListOfRoutesViewModel) {
    val size = remember { mutableStateOf(IntSize.Zero) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .then(
                    with(LocalDensity.current) {
                        Modifier.size(
                            width = 70.dp,
                            height = ((size.value.height).toDp().value).dp,
                        )
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .then(
                        with(LocalDensity.current) {
                            Modifier.size(
                                width = 2.dp,
                                height = ((size.value.height).toDp().value - 40).dp,
                            )
                        }
                    )
                    .align(Alignment.TopCenter)
                    .background(color = getRouteColor(route))
            ) {}
            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .absolutePadding(bottom = 20.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_source_desti),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = getRouteColor(route)),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
                .absolutePadding(right = 10.dp, bottom = 10.dp)
                .onSizeChanged {
                    size.value = it
                },
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0XFFE8F0F0))
                    .clickable {
                        setupCameraPositionAndZoom(viewModel, route)
                        viewModel.mapsHeight.value = 0.5f
                    }
                    .absolutePadding(left = 20.dp, right = 10.dp, top = 20.dp, bottom = 20.dp)

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
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Source",
                    fontSize = 14.sp,
                    color = Color(0xFFFFAA05)
                )
            }
        }
    }
}

private data class DottedShape(
    val step: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(Path().apply {
        val stepPx = with(density) { step.toPx() }
        val stepsCount = (size.width / stepPx).roundToInt()
        val actualStep = size.width / stepsCount
        val dotSize = Size(width = actualStep / 2, height = size.height)
        for (i in 0 until stepsCount) {
            addRect(
                Rect(
                    offset = Offset(x = i * actualStep, y = 0f),
                    size = dotSize
                )
            )
        }
        close()
    })
}

@Composable
fun MapsComponent(
    modifier: Modifier,
    navHostController: NavHostController,
    viewModel: ListOfRoutesViewModel
) {
    Box(modifier) {
        LoadMapView(
            viewModel = viewModel, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
        HeaderBackButton(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            navHostController
        )
    }
}

fun bitMapFromVector(vectorResID: Int, context: Context): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResID)
    vectorDrawable!!.setBounds(
        0,
        0,
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight
    )
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
private fun HeaderBackButton(
    modifier: Modifier,
    navHostController: NavHostController
) {
    Box(modifier = modifier) {
        Row(
            modifier = modifier
                .align(Alignment.CenterStart)
                .absolutePadding(left = 20.dp, top = 20.dp, right = 20.dp)
                .clickable { navHostController.navigate(Screens.Home.route) },
            verticalAlignment = Alignment.CenterVertically
        ) {
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

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .absolutePadding(right = 20.dp, top = 20.dp)
                .background(
                    color = Color(0xFFC4d2d2),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(all = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = "exclamation",
                modifier = Modifier
                    .size(15.dp)
                    .clickable { navHostController.navigate(Screens.Home.route) })
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Report",
                color = Color.DarkGray
            )
        }
    }
}

