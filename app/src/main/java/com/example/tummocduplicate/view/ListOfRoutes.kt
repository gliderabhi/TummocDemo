package com.example.tummocduplicate.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tummocduplicate.R
import com.example.tummocduplicate.bean.Route
import com.example.tummocduplicate.bean.RouteMediumEnum
import com.example.tummocduplicate.bean.TummocBaseJsonItem
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ListOfRoutes(viewModel: ListOfRoutesViewModel) {
    val possibleRoutes = remember {
        mutableStateOf(ArrayList<TummocBaseJsonItem>())
    }
    LaunchedEffect(key1 = viewModel.possibleRoutes.value) {
        possibleRoutes.value = viewModel.possibleRoutes.value ?: ArrayList()
    }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFd3d3d3)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (possibleRoutes.value.isNotEmpty()) {
            Log.d("listOfRoutes " , possibleRoutes.value.toString())
            HeaderToFromLayout(possibleRoutes)
            Spacer(modifier = Modifier.height(50.dp))
            MiddleSection(clickHandle = {
                coroutineScope.launch(Dispatchers.IO) {
                    val list: ArrayList<TummocBaseJsonItem>
                    possibleRoutes.value = viewModel.possibleRoutes.value ?: ArrayList()
                    when (it) {
                        0 -> {
                            list = ArrayList()
                            for (v in possibleRoutes.value) {
                                for (r in v.routes) {
                                    if (r.medium == RouteMediumEnum.Train.value) {
                                        list.add(v)
                                        break
                                    }
                                }
                            }
                            possibleRoutes.value = list
                        }
                        1 -> {
                            list = ArrayList()
                            for (v in possibleRoutes.value) {
                                for (r in v.routes) {
                                    if (r.medium == RouteMediumEnum.Bus.value) {
                                        list.add(v)
                                        break
                                    }
                                }
                            }
                            possibleRoutes.value = list
                        }
                        2 -> {
                            list = ArrayList()
                            for (v in possibleRoutes.value) {
                                for (r in v.routes) {
                                    if (r.medium == RouteMediumEnum.Walk.value) {
                                        list.add(v)
                                        break
                                    }
                                }
                            }
                            possibleRoutes.value = list
                        }


                    }
                }
            })
            Spacer(modifier = Modifier.height(50.dp))
            RoutesList(viewModel, possibleRoutes)
        } else {
            CircularProgressIndicator(modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun RoutesList(
    viewModel: ListOfRoutesViewModel,
    possibleRoutes: MutableState<ArrayList<TummocBaseJsonItem>>
) {
    AnimatedVisibility(
        visible = /*metroClicked.value*/ true,
        enter = slideInVertically(animationSpec = tween(2000), initialOffsetY = { 3000 }),
        exit = slideOutVertically(
            animationSpec = tween(durationMillis = 2000),
            targetOffsetY = { 3000 })
    ) {
        Column() {
            Text(
                text = "Fastest Route",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(left = 10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            BottomList(viewModel, possibleRoutes)
        }
    }
}

@Composable
fun BottomList(
    viewModel: ListOfRoutesViewModel,
    possibleRoutes: MutableState<ArrayList<TummocBaseJsonItem>>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(bottom = 20.dp)
    ) {
        items(count = possibleRoutes.value.size, itemContent = {
            RoutesComposable(possibleRoutes.value.get(it), viewModel)
        })
    }
}

@Composable
fun RoutesComposable(routes: TummocBaseJsonItem, viewModel: ListOfRoutesViewModel) {
    var selected by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (selected) 0.7f else 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(left = 5.dp, right = 5.dp, bottom = 5.dp)
            .pointerInput(Unit) {
                while (true) {
                    awaitPointerEventScope {
                        awaitFirstDown(false)
                        selected = true
                        waitForUpOrCancellation()
                        selected = false
                    }
                }
            }
            .clickable {
                viewModel.clickedRoute = routes.routes
                viewModel.navController.navigate(Screens.Maps.route)
            }
            .border(width = 0.5.dp, shape = RoundedCornerShape(20.dp), color = Color.DarkGray)
            .scale(scale),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .absolutePadding(left = 10.dp, right = 10.dp, bottom = 20.dp, top = 10.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_error),
                    contentDescription = "exclamation icon",
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Report", color = Color(0xFFd3d3d3), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                for (i in routes.routes) {
                    ItemBarAndCategoryIcon(i)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            SequenceOfPaths(routes, viewModel = viewModel)
            Spacer(modifier = Modifier.height(10.dp))
            StaticDataForJourney(routes)
        }
    }
}

@Composable
private fun SequenceOfPaths(routes: TummocBaseJsonItem, viewModel: ListOfRoutesViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(count = routes.routes.size, itemContent = {
                if (it != 0) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_forward_24),
                        contentDescription = "forward icon",
                        modifier = Modifier.size(15.dp)
                    )
                }
                Image(
                    painter = painterResource(
                        id = getRouteDrawable(routes.routes[it].medium)
                    ),
                    contentDescription = "walk",
                    modifier = Modifier
                        .size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                val routeTextDurationOrPath = when (routes.routes[it].medium) {
                    RouteMediumEnum.Walk.value -> viewModel.getTotalTimeInMinutes(routes.routes[it].duration)
                        .toString() + " min"
                    else -> {
                        routes.routes[it].busRouteDetails?.routeNumber.toString()
                    }
                }
                val textColor = when (routes.routes[it].medium) {
                    RouteMediumEnum.Walk.value -> Color.Black
                    else -> Color(0xFFFFA005)
                }
                Text(
                    text = routeTextDurationOrPath,
                    fontSize = 12.sp,
                    color = textColor
                )
            })
        }
    }
}

@Composable
private fun StaticDataForJourney(routes: TummocBaseJsonItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "NEXT SCHEDULED",
                fontSize = 10.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            if (routes.routes.isNotEmpty()) {
                Text(
                    text = "${getTime(routes.routes[0].sourceTime)} pm",
                    fontSize = 8.sp,
                    color = Color(0xFFFFA005),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column {
            Text(
                text = "EXTIMATED PRICE",
                fontSize = 10.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "\u20B9 ${routes.totalFare}",
                fontSize = 8.sp,
                color = Color(0xFFFFA005),
                fontWeight = FontWeight.Bold
            )
        }
        Column {
            Text(
                text = "TRAVEL TIME",
                fontSize = 10.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "~ ${getTime(routes.totalDuration)}",
                fontSize = 8.sp,
                color = Color(0xFFFFA005),
                fontWeight = FontWeight.Bold
            )
        }

        Column {
            Text(
                text = "DISTANCE",
                fontSize = 10.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${routes.totalDistance} km",
                fontSize = 8.sp,
                color = Color(0xFFFFA005),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun getTime(sourceTime: List<String>): String {
    return try {
        val time = sourceTime.get(0)
        val t2 = time.split("[", "]")[0].split(":")
        "${t2[0]}:${t2[1]}"
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

@Composable
private fun ItemBarAndCategoryIcon(i: Route) {
    Column(
        modifier = Modifier
            .fillMaxWidth(i.weight ?: 1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    color = getRouteColor(i)
                )
        ) {}
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .size(30.dp),
            elevation = 10.dp,
            shape = RoundedCornerShape(30.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(
                        id = getRouteDrawable(i.medium)
                    ),
                    contentDescription = "walk",
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

fun getRouteDrawable(i: String) = when (i) {
    RouteMediumEnum.Bus.value -> R.drawable.ic_bus
    RouteMediumEnum.Walk.value -> R.drawable.ic_walk
    RouteMediumEnum.Train.value -> R.drawable.ic_train
    RouteMediumEnum.Metro.value -> R.drawable.ic_train
    else -> R.drawable.ic_bus
}

@Composable
private fun MiddleSection(clickHandle: (Int) -> Unit) {
    val selectedIndex = remember {
        mutableStateOf<Int?>(null)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Row(modifier = Modifier.clickable {
            clickHandle(0)
            selectedIndex.value = 0
        }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_train),
                contentDescription = "train/metro",
                colorFilter = ColorFilter.tint(color = if (selectedIndex.value == 0) Color.Black else Color.Gray)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Metro")
        }
        Row(modifier = Modifier.clickable {
            clickHandle(1)
            selectedIndex.value = 1
        }) {
            Image(
                painter = painterResource(id = R.drawable.ic_bus),
                contentDescription = "Bus",
                colorFilter = ColorFilter.tint(color = if (selectedIndex.value == 1) Color.Black else Color.Gray)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Bus")
        }
        Row(modifier = Modifier.clickable {
            clickHandle(2)
            selectedIndex.value = 2
        }) {
            Image(
                painter = painterResource(id = R.drawable.ic_walk),
                contentDescription = "Walk",
                colorFilter = ColorFilter.tint(color = if (selectedIndex.value == 2) Color.Black else Color.Gray)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Walk")
        }
    }
}

@Composable
private fun HeaderToFromLayout(possibleRoutes: MutableState<ArrayList<TummocBaseJsonItem>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(top = 4.dp, bottom = 4.dp, left = 4.dp, right = 4.dp),
        elevation = 10.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(5.dp))
            Column() {
                Spacer(modifier = Modifier.height(30.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back icon",
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(right = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Source",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
//                Spacer(modifier = Modifier.height(5.dp))
                if (possibleRoutes.value.isNotEmpty() && possibleRoutes.value[0].routes.isNotEmpty()) {
                    DestinationDetails(possibleRoutes.value[0].routes[0].sourceTitle ?: "Source")
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Destination",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
//                Spacer(modifier = Modifier.height(5.dp))

                if (possibleRoutes.value.isNotEmpty() && possibleRoutes.value[0].routes.isNotEmpty()) {
                    DestinationDetails(
                        possibleRoutes.value[0].routes[possibleRoutes.value[0].routes.size - 1].destinationTitle
                            ?: "Destination"
                    )
                }
            }
        }
    }
}

@Composable
fun DestinationDetails(sourceTitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row() {
            Row(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(color = Color.Red)
            ) {}
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .absolutePadding(right = 70.dp)
            ) {
                Text(
                    text = sourceTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
//                fontFamily = FontFamily.Cursive,
                    fontStyle = FontStyle.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
//            Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Random nonsense text",
                    fontSize = 12.sp,
                    color = Color(0xFFFFA500),
                    fontWeight = FontWeight.Normal,
//                fontFamily = FontFamily.Cursive,
                    fontStyle = FontStyle.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }

        val size = remember {
            mutableStateOf(20)
        }
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(30.dp)
                .absolutePadding(right = 30.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_like_icon),
                contentDescription = "like",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(size.value.dp)
                    .clickable {
                        size.value = if (size.value == 20)
                            30
                        else
                            20
                    },
                colorFilter = ColorFilter.tint(color = Color.Gray)
            )
        }
    }
}

fun getRouteColor(
    route: Route
) = when (route.medium) {
    RouteMediumEnum.Bus.value -> Color(0xFFF3BB01)
    RouteMediumEnum.Walk.value -> Color(0xFF152238)
    RouteMediumEnum.Train.value -> Color(0xFFCD5E77)
    RouteMediumEnum.Metro.value -> Color.Red
    else -> Color.Blue
}
