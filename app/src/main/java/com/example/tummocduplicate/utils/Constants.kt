package com.example.tummocduplicate.utils

import android.content.Context
import com.example.tummocduplicate.view.MainActivity
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*

object Constants {

    var mActivity : MainActivity? = null

    fun loadJSONFromAsset(name: String?, context: Context): String {
        var json: String? = null
        val obj: JSONObject? = null
        try {
            val am = context.assets
            val mapList =
                Arrays.asList(*am.list(""))
            if (mapList != null && mapList.size > 0 && mapList.contains(name)) {
                if (context != null) {
                    val `is` = context.assets.open(name!!)
                    if (`is` != null) {
                        val size = `is`.available()
                        val buffer = ByteArray(size)
                        `is`.read(buffer)
                        `is`.close()
                        json = String(buffer, StandardCharsets.UTF_8)
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
        return json.toString()
    }
}