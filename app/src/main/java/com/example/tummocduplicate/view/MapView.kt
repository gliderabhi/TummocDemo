package com.example.tummocduplicate.view

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.tummocduplicate.R
import com.example.tummocduplicate.bean.RouteMediumEnum
import com.example.tummocduplicate.viewModel.ListOfRoutesViewModel
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.CameraPosition
import com.google.android.libraries.maps.model.LatLng
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
    val polyLines = remember {
        mutableStateOf<HashMap<LatLng, String>>(HashMap())
    }
    val polyLinesList = remember {
        mutableListOf<LatLng>()
    }
    LaunchedEffect(key1 = viewModel.clickedRoute) {
        setupCameraPositionAndZoom(viewModel, clickedRoutes[0])
        launch(Dispatchers.IO) {
            for (i in viewModel.clickedRoute) {
                val latLong1 = LatLng(i.sourceLat, i.sourceLong)
                val latLong2 = LatLng(i.destinationLat, i.destinationLong)
                polyLines.value[latLong1] = i.medium
                polyLines.value[latLong2] = ""
                polyLinesList.add(latLong1)
                polyLinesList.add(latLong2)
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
    Box(modifier = modifier) {
        AndroidView({ mapView }, modifier = modifier) {
            CoroutineScope(Dispatchers.Main).launch {
                val map = mapView.awaitMap()
                map.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        cameraPosition.value
                    )
                )
                for (i in clickedRoutes.indices - 1) {
                    map.addPolyline(
                        PolylineOptions().add(polyLinesList[i], polyLinesList[i + 1]).color(
                            when (clickedRoutes[i].medium) {
                                RouteMediumEnum.Bus.value -> Color(0xFFF3BB01).value.toInt()
                                RouteMediumEnum.Walk.value -> Color(0xFF152238).value.toInt()
                                RouteMediumEnum.Train.value -> Color(0xFFCD5E77).value.toInt()
                                RouteMediumEnum.Metro.value -> Color.Red.value.toInt()
                                else -> Color.Blue.value.toInt()
                            }
                        )
                    )
                }

            }

        }
    }
}
