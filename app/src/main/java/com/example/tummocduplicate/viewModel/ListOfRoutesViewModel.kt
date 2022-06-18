package com.example.tummocduplicate.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.tummocduplicate.bean.Route
import com.example.tummocduplicate.bean.RouteMediumEnum
import com.example.tummocduplicate.bean.TummocBaseJsonItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.maps.android.compose.MapProperties

class ListOfRoutesViewModel : ViewModel() {

    lateinit var mapProperties : MapProperties
    lateinit var navController: NavHostController
    var firebaseRefrence: FirebaseDatabase? = null
    var response: Any? = null
    val possibleRoutes = mutableStateOf<ArrayList<TummocBaseJsonItem>?>(ArrayList())
    lateinit var clickedRoute : List<Route>

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
}