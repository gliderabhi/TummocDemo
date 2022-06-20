package com.example.tummocduplicate.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Trails(
    var distance: Double = 0.0,
    var fareStage: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var name: String = "",
    var seq: @RawValue Any? = null,
    var time: @RawValue Any? = null
) : Parcelable