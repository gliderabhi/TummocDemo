package com.example.tummocduplicate.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TummocBaseJsonItem(
    var routeTitle: String = "",
    var routes: List<Route> = listOf(),
    var totalDistance: Double = 0.0,
    var totalDuration: String = "",
    var totalFare: Double = 0.0
) : Parcelable