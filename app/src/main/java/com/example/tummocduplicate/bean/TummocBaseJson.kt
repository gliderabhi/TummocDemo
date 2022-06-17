package com.example.tummocduplicate.bean

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(tableName = "TummocBaseJson")
class TummocBaseJson(
    val items: @RawValue List<TummocBaseJsonItem>? = null
) : Parcelable