package com.example.tummocduplicate.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.tummocduplicate.bean.MapData
import com.example.tummocduplicate.bean.Route
import com.example.tummocduplicate.bean.RouteMediumEnum
import com.example.tummocduplicate.bean.TummocBaseJsonItem
import com.google.android.libraries.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.math.pow

class ListOfRoutesViewModel : ViewModel() {

    val mapsHeight = mutableStateOf(0.5f)
    val permissionGranted = mutableStateOf(false)
    val zoomValuesList = HashMap<Int, Int>()
    var zoomValues: MutableState<Float> = mutableStateOf(9f)
    var cameraPositionLatLong: MutableState<LatLng> = mutableStateOf(LatLng(0.0, 0.0))
    lateinit var navController: NavHostController
    var firebaseRefrence: FirebaseDatabase? = null
    var response: Any? = null
    val possibleRoutes = mutableStateOf<ArrayList<TummocBaseJsonItem>?>(ArrayList())
    lateinit var clickedRoute: List<Route>

    suspend fun setupZoomValues() {
        for (i in 0..25) {
            zoomValuesList[i] = (40075016 / 2.0.pow(i * 1.0).toInt())
        }
    }

    suspend fun getDataFromFirebase() {
        firebaseRefrence = FirebaseDatabase.getInstance()
        firebaseRefrence?.getReference("tummoc")
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        response = p0.value
                        val json = Gson().fromJson(
                            response.toString(),
                            Array<TummocBaseJsonItem>::class.java
                        )
                        val itemsList: ArrayList<TummocBaseJsonItem> = ArrayList()
                        for (i in json) {
                            itemsList.add(i)
                            for (v in i.routes) {
                                v.weight = when(v.medium) {
                                    RouteMediumEnum.Walk.value -> 0.3f
                                    RouteMediumEnum.Bus.value -> 0.5f
                                    RouteMediumEnum.Train.value -> 0.3f
                                    RouteMediumEnum.Metro.value -> 0.5f
                                    else -> 1f
                                }
                                if(i.routes.indexOf(v) == i.routes.size-1) v.weight = 1f
                            }
                        }
                        possibleRoutes.value = itemsList
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun getTotalTimeInMinutes(time: String): Long {
        val times = time.split(":")
        return times[0].toLong() * 24 + times[1].toLong() * 60 + times[2].toLong()
    }

    suspend fun getDetails(origin: LatLng, dest: LatLng): ArrayList<List<LatLng>> {
        val client = OkHttpClient()
        val url = getDirectionURL(origin, dest)
        println("url for polyline $url")
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val data = response.body!!.string()
        println("response from server $data")
        val result = ArrayList<List<LatLng>>()
        try {
            val respObj = Gson().fromJson(data, MapData::class.java)
            val path = ArrayList<LatLng>()
            for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
            }
            result.add(path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun getDirectionURL(origin: LatLng, dest: LatLng): String {
        return "https://api.openrouteservice.org/v2/directions/driving-car?api_key=5b3ce3597851110001cf624873ba05add9444553b0537e2827b63f99" +
                "&start=${origin.latitude},${origin.longitude}&end=${dest.latitude},${dest.longitude}"
    }

    suspend fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }
}