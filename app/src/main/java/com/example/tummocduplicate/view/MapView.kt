package com.example.tummocduplicate.view

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.tummocduplicate.R
import com.example.tummocduplicate.bean.RouteMediumEnum
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.CameraPosition
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }


@Composable
fun LoadMapView(
    viewModel: ListOfRoutesViewModel,
    modifier: Modifier
) {
    val mapView = rememberMapViewWithLifecycle()
    val clickedRoutes = viewModel.clickedRoute
    val polyLinesList = remember {
        mutableStateOf<ArrayList<PolylineOptions>>(ArrayList())
    }
    val points = remember {
        mutableStateOf<HashMap<LatLng, String>>(HashMap())
    }
    LaunchedEffect(key1 = viewModel.clickedRoute) {
        setupCameraPositionAndZoom(viewModel, clickedRoutes[0])
        launch(Dispatchers.IO) {
            for (i in viewModel.clickedRoute) {
                val src = LatLng(i.sourceLat, i.sourceLong)
                val dest = LatLng(i.destinationLat, i.destinationLong)

                points.value[src] = i.medium
                points.value[dest] = ""
                val routesColor = getRoutesColor(i.medium)
                val line = PolylineOptions().add(
                    src,
                    dest
                ).width(
                    when {
                        i.distance * 1000 < 1 -> 10f
                        i.distance * 1000 < 10 -> 5f
                        i.distance * 1000 < 50 -> 4f
                        else -> 2f
                    }
                ).geodesic(true).clickable(true).color(routesColor)
                polyLinesList.value.add(line)
            }
        }
    }
    val cameraPosition = remember {
        mutableStateOf(
            CameraPosition(
                viewModel.cameraPositionLatLong.value,
                viewModel.zoomValues.value,
                0f, 0f
            )
        )
    }
    LaunchedEffect(key1 = viewModel.cameraPositionLatLong.value) {
        cameraPosition.value = CameraPosition(
            viewModel.cameraPositionLatLong.value,
            viewModel.zoomValues.value,
            0f, 0f
        )
    }
    val context = LocalContext.current
    Box(modifier = modifier) {
        AndroidView({ mapView }, modifier = modifier) {
            CoroutineScope(Dispatchers.Main).launch {
                val map = mapView.awaitMap()
                map.isTrafficEnabled = true
                map.uiSettings.setAllGesturesEnabled(true)
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
                map.isBuildingsEnabled = true
                map.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        cameraPosition.value
                    )
                )

                for (i in points.value.iterator()) {
                    map.addMarker(
                        MarkerOptions().position(i.key)
                            .icon(bitMapFromVector(getRouteDrawable(i.value), context))
                    )
                }
                for (i in polyLinesList.value) {
                    map.addPolyline(i)
                }
                map.setOnPolylineClickListener {
                    it.width = 10f

                }
            }

        }
    }
}

fun getRoutesColor(medium: String): Int = when (medium) {
    RouteMediumEnum.Bus.value -> R.color.bus_color
    RouteMediumEnum.Walk.value -> R.color.walk_color
    RouteMediumEnum.Train.value -> R.color.train_walk
    else -> R.color.bus_color
}



