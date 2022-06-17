package com.example.tummocduplicate.bean

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TummocConverter {

    @TypeConverter
    fun tummocItemsConverter( list : List<TummocBaseJsonItem>? ): String? {
        if (list == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<TummocBaseJsonItem>>() {
        }.type
        return gson.toJson(list, type)
    }
}