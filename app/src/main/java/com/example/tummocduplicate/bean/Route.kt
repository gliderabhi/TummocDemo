package com.example.tummocduplicate.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Route(
    var busRouteDetails: BusRouteDetails? = null,
    var destinationLat: Double = 0.0,
    var destinationLong: Double = 0.0,
    var destinationTime: List<String> = listOf(),
    var destinationTitle: String = "",
    var distance: Double = 0.0,
    var duration: String = "",
    var fare: Double = 0.0,
    var medium: String = "",
    var rideEstimation: @RawValue Any? = null,
    var routeName: @RawValue Any? = null,
    var sourceLat: Double = 0.0,
    var sourceLong: Double = 0.0,
    var sourceTime: List<String> = listOf(),
    var sourceTitle: String = "",
    var trails:  List<Trails>? = listOf(),
    var weight : Float? = null
) : Parcelable